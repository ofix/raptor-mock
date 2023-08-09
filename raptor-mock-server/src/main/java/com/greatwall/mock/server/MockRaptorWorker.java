
package com.greatwall.mock.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class MockRaptorWorker implements Runnable {
    private Thread thread;
    private String name;
    private Selector workerSelector;
    private volatile boolean started = false;

    public MockRaptorWorker(String name) {
        this.name = name;
    }

    // 将新到来的客户socket 添加到并发队列
    public void addNewClientSocket(SocketChannel clientChannel) {
        ClientSession session = new ClientSession();
        try {
            clientChannel.register(workerSelector, SelectionKey.OP_READ, session);
            workerSelector.wakeup(); // 唤醒子进程
        } catch (ClosedChannelException e) {
            System.out.println(e.getMessage());
        }
    }

    // 初始化线程和Selector
    public void register() throws IOException {
        if (!started) {
            this.workerSelector = Selector.open();
            this.thread = new Thread(this, name);
            this.thread.start();
            started = true;
        }
    }

    /**
     * @todo 根据文件路径获取本地JSON文件完整内容
     * @param jsonFilePath JSON文件绝对路径
     * @return
     * @author songhuabiao@greatwall.com.cn
     */
    public static String loadJsonFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        String fileContent = new String(Files.readAllBytes(path));
        return fileContent;
    }

    @Override
    public void run() {
        while (true) {
            try {
                workerSelector.select();
                Iterator<SelectionKey> itr = workerSelector.selectedKeys().iterator();
                while (itr.hasNext()) {
                    SelectionKey key = itr.next();
                    itr.remove();
                    if (key.isReadable()) {
                        ClientSession session = (ClientSession) key.attachment();
                        session.processClientRequest(key);
                        key.attach(session);
                    } else if (key.isWritable()) {
                        ClientSession session = (ClientSession) key.attachment();
                        session.processClientResponse(key, workerSelector);
                        key.attach(session);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

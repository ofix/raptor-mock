package com.greatwall.mock.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class MockRaptorServer {
    public static void run(String[] args) throws IOException {
        // Thread.currentThread().setDaemon(true);
        Thread.currentThread().setName("raptor-manager");
        // 启动服务器
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // 设置非阻塞监听模式
        serverChannel.configureBlocking(false);
        // 开启多路复用选择器，达到一个线程监听多个客户端套接字的目的
        Selector managerSelector = Selector.open();
        SelectionKey managerKey = serverChannel.register(managerSelector, 0, null);
        managerKey.interestOps(SelectionKey.OP_ACCEPT); // 主线程监听客户事件
        // 绑定服务器端口20000
        serverChannel.bind(new InetSocketAddress("localhost", 20000));

        MockRaptorWorker[] workers = new MockRaptorWorker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new MockRaptorWorker("raptor-worker-" + i);
            workers[i].register();
        }

        System.out.println("Mock server started!");

        AtomicInteger index = new AtomicInteger(0);
        for (;;) { // 主线程 accept 循环
            managerSelector.select(); // Selector 检查哪些客户端连接有网络读写事件，如果没有(新客户端连接、读、写，客户端断开)等事件中断，主线程将睡眠 (阻塞)
            Iterator<SelectionKey> itr = managerSelector.selectedKeys().iterator();
            System.out.println("Mock server loop!");
            while (itr.hasNext()) { // 遍历网络可读写事件进行处理
                SelectionKey key = itr.next();
                itr.remove(); // 移除客户端到达的事件，避免重复消费事件
                if (key.isAcceptable()) { // 如果是新客户连接到来，我
                    SocketChannel clientChannel = serverChannel.accept();
                    // 新到来的客户端连接必须设置为非阻塞模式，否则Selector检查这个客户连接网络事件的时候会阻塞
                    clientChannel.configureBlocking(false);
                    System.out.println("new client connected: " + clientChannel.getRemoteAddress());
                    System.out.println("start register client: " + clientChannel.getRemoteAddress());
                    workers[index.getAndIncrement() % workers.length].addNewClientSocket(clientChannel);
                    System.out.println("end register client:" + clientChannel.getRemoteAddress());
                    // 新客户连接需要Selector监听读事件
                }
            }
        }
    }
}

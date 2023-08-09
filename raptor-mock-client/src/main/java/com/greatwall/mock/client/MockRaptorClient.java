package com.greatwall.mock.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;
import java.util.Timer;

public class MockRaptorClient {

    public static void run(String[] args) throws IOException {
        // SocketChannel clientChannel = SocketChannel.open();
        // clientChannel.connect(new InetSocketAddress("localhost", 20000));
        // SocketAddress address = clientChannel.getLocalAddress();

        // clientChannel.write(Charset.defaultCharset().encode("abc"));
        // System.out.println("client address: " + address);
        // System.in.read();

        if (args.length < 2) {
            System.out.println("输入参数错误!");
            System.out.println("输入格式 java -jar raptor-mock-client-1.0.jar 主机名 端口号,例如:");
            System.out.println("java -jar raptor-mock-client-1.0.jar localhost 20000");
            return;
        }

        Socket socket = new Socket(args[0], Integer.valueOf(args[1]));

        final InputStream in = socket.getInputStream();
        final OutputStream out = socket.getOutputStream();
        final DataInputStream dis = new DataInputStream(in); // 字节输入流
        final DataOutputStream dos = new DataOutputStream(out);// 字节输出流

        final int MAX_RUNNING_COUNT = 120;
        final Timer timerRecv = new Timer();
        final String[] messages = { "Hello,Server! I'm Client vvvvv", "今天是个好天气！" };

        final Timer timerSend = new Timer();
        TimerTask timerSendTask = new TimerTask() {
            int running_count = 0;

            @Override
            public void run() {
                if (running_count >= MAX_RUNNING_COUNT) {
                    timerSend.cancel();
                }
                running_count += 1;

                String msg = messages[Utils.rand(0, 1)];
                byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
                // 发送二进制消息
                BinaryWriter writer = new BinaryWriter(24 + msgBytes.length);
                writer.writeShort((short) 1, ByteSequence.BIG_ENDIGAN) // 命令标识, 默认为1
                        .writeInt(msgBytes.length, ByteSequence.BIG_ENDIGAN) // 数据包大小, 4个字节
                        .writeShort((short) 2, ByteSequence.BIG_ENDIGAN) // 命令码, 2个字节
                        .writeLong(System.currentTimeMillis(), ByteSequence.BIG_ENDIGAN) // 时间戳,无符号长长整型(64位，大端字节序)
                        .writeLong(System.currentTimeMillis(), ByteSequence.BIG_ENDIGAN) // 流水号,无符号长长整型(64位，大端字节序)
                        .writeString(msg);
                byte[] bytes = writer.getBytes();
                System.out.println("++++++++++  发送消息给服务端  ++++++++++");
                System.out.println(msg);
                try {
                    dos.write(bytes);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    timerSend.cancel();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    timerSend.cancel();
                }
            }
        };

        timerSend.schedule(timerSendTask, 0, 1000);

        TimerTask timerRecvTask = new TimerTask() {
            int running_count = 0;

            @Override
            public void run() {
                if (running_count >= MAX_RUNNING_COUNT) {
                    timerRecv.cancel();
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                running_count += 1;
                // 获取二进制消息, 定时器重复构建socket输入流，程序会崩溃
                // DataInputStream dis = new DataInputStream(in);
                int size = 0;
                try {
                    byte[] sizeBuf = new byte[4];
                    int remainingBytes = 4;
                    while (remainingBytes > 0) {
                        int nBytes = dis.read(sizeBuf);
                        if (nBytes != -1) {
                            remainingBytes -= nBytes;
                        } else {
                            break;
                        }
                    }

                    BinaryReader reader = new BinaryReader(sizeBuf);
                    size = reader.readInt(ByteSequence.BIG_ENDIGAN);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    timerRecv.cancel();
                    try {
                        socket.close();
                    } catch (IOException e2) {
                        System.out.println(e2.getMessage());
                    }
                }
                System.out.println("++++++++++  接收服务端消息  ++++++++++");
                System.out.println("接收消息长度 = " + size);
                if (size > 0) {
                    byte[] serverData = new byte[size];
                    try {
                        dis.readFully(serverData, 0, size);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        timerRecv.cancel();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        timerRecv.cancel();
                    }
                    String serverMessage = new String(serverData, StandardCharsets.UTF_8);
                    System.out.println(serverMessage);
                } else {
                    System.out.println("服务端未发送任何消息");
                }
            }
        };

        timerRecv.schedule(timerRecvTask, 10, 1000);

        // socket.close();

    }
}

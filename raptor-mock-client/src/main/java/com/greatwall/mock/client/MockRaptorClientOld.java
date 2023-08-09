package com.greatwall.mock.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class MockRaptorClientOld {

    // client send message
    // byte[] message = ...
    // Socket socket = ...
    // DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

    // dOut.writeInt(message.length); // write length of the message
    // dOut.write(message); // write the message

    // -----------------------------------------------------
    // Socket socket = ...
    // DataInputStream dIn = new DataInputStream(socket.getInputStream());

    // int length = dIn.readInt(); // read length of incoming message
    // if(length>0) {
    // byte[] message = new byte[length];
    // dIn.readFully(message, 0, message.length); // read the message
    // }

    // -----------------------------------------------------
    // DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
    // float f = dis.readFloat();

    // //or if it's an int:
    // int i = dis.readInt();

    // -----------------------------------------------------
    // byte [] data = new byte[] {1,2,3,4};
    // ByteBuffer b = ByteBuffer.wrap(data);
    // System.out.println(b.getInt());
    // System.out.println(b.getFloat());

    public static void run(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        if (args.length < 2) {
            System.out.println("输入参数错误!");
            System.out.println("输入格式 java -jar raptor-mock-client-1.0.jar 主机名 端口号,例如:");
            System.out.println("java -jar raptor-mock-client-1.0.jar localhost 20000");
            return;
        }

        Socket socket = new Socket(args[0], Integer.valueOf(args[1]));

        final InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();// 字节输出流
        final DataOutputStream dos = new DataOutputStream(out);

        final int MAX_RUNNING_COUNT = 120;
        final Timer timerRecv = new Timer();
        final String[] messages = { "Hello,Server! I'm Client", "今天是个好天气！" };

        TimerTask timerRecvTask = new TimerTask() {
            int running_count = 0;

            @Override
            public void run() {
                if (running_count >= MAX_RUNNING_COUNT) {
                    timerRecv.cancel();
                }
                running_count += 1;
                // 获取二进制消息
                DataInputStream dis = new DataInputStream(in);
                int length = 0;
                try {
                    length = dis.readInt();
                } catch (IOException e) {
                    e.printStackTrace();
                    timerRecv.cancel();
                }
                System.out.println("++++++++++  接收服务端消息  ++++++++++");
                System.out.println("接收消息长度 = " + length);
                if (length > 0) {
                    byte[] serverData = new byte[length];
                    try {
                        dis.readFully(serverData, 0, length);
                    } catch (IOException e) {
                        e.printStackTrace();
                        timerRecv.cancel();
                    }
                    String serverMessage = new String(serverData, StandardCharsets.UTF_8);
                    System.out.println(serverMessage);
                } else {
                    System.out.println("服务端未发送任何消息");
                }
            }
        };

        timerRecv.schedule(timerRecvTask, 0, 1000);

        final Timer timerSend = new Timer();
        TimerTask timerSendTask = new TimerTask() {
            int running_count = 0;

            @Override
            public void run() {
                if (running_count >= MAX_RUNNING_COUNT) {
                    timerRecv.cancel();
                }
                running_count += 1;
                // 发送二进制消息
                BinaryWriter writer = new BinaryWriter(28);
                writer.writeShort((short) 1, ByteSequence.BIG_ENDIGAN) // 命令标识, 默认为1
                        .writeInt(28, ByteSequence.BIG_ENDIGAN) // 数据包大小, 4个字节
                        .writeShort((short) 2, ByteSequence.BIG_ENDIGAN) // 命令码, 2个字节
                        .writeLong(System.currentTimeMillis(), ByteSequence.BIG_ENDIGAN) // 时间戳,无符号长长整型(64位，大端字节序)
                        .writeLong(System.currentTimeMillis(), ByteSequence.BIG_ENDIGAN) // 流水号,无符号长长整型(64位，大端字节序)
                        .writeString(messages[Utils.rand(0, 1)]);
                byte[] bytes = writer.getBytes();
                System.out.println("++++++++++  发送消息给服务端  ++++++++++");
                System.out.println("AbcD");
                try {
                    dos.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        timerSend.schedule(timerSendTask, 0, 1000);
        socket.close();

    }
}
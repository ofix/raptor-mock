package com.greatwall.mock.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.OutputStream;

import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Random;

/**
 * @author songhaubiao@greatwall.com.cn
 * @date 2023-08-03 16:01
 */
public class MockRaptorServerOld {

    public static class PacketHead { // 数据包包头
        short identifier; // 命令标识, 默认为1, 2个字节, 无符号长整型(16位，大端字节序)
        int length; // 数据包大小, 4个字节,无符号短整型32位，大端字节序)
        short command; // 命令码, 2个字节,无符号短整型16位，大端字节序)
        long timeStamp1; // 时间戳,无符号长长整型(64位，大端字节序)
        long timeStamp2; // 流水号,无符号长长整型(64位，大端字节序)
    }

    public static PacketHead parsePacket(Object object) {
        return null;
    }

    // 读取本地JSON文件
    public static String readLocalFile(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        Reader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
        int ch = 0;
        StringBuffer sb = new StringBuffer();
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        fileReader.close();
        reader.close();
        String jsonStr = sb.toString();
        return jsonStr;
    }

    public static String readJsonFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        String fileContent = new String(Files.readAllBytes(path));
        return fileContent;
    }

    public static void run(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(20000);
        System.out.println("Mock BMC Server has been started!");
        Socket clientSocket = serverSocket.accept();
        while (true) {
            InputStream inStream = clientSocket.getInputStream();
            OutputStream outStream = clientSocket.getOutputStream();

            // 解析客户端二进制数据包, 没有数据到来，进程会睡眠
            DataInputStream dis = new DataInputStream(inStream);
            short identifier = dis.readShort();
            int length = dis.readInt();
            short command = dis.readShort();
            dis.readLong(); // 时间戳
            dis.readLong(); // 序列号

            System.out.println("++++++++++  接收客户端数据  ++++++++++");
            System.out.println("identifier: " + identifier + " , length: " + length + " command: " + command);
            if (length > 24) {
                byte[] clientData = dis.readNBytes(length - 24);
                String clientMessage = new String(clientData, StandardCharsets.UTF_8);
                System.out.println(clientMessage);
            } else {
                System.out.println("客户未发送任何消息");
            }

            // 根据客户发来的命令加载不同的JSON模拟数据
            String responseData = "";
            File directory = new File("../data/BatchServerPowerOn.json");
            String jsonFilePath = directory.getCanonicalPath();
            switch (command) {
                case 1:
                    responseData = readJsonFile(jsonFilePath);
                    break;
                case 2:
                    responseData = readJsonFile(jsonFilePath);
                    break;
                default:
                    break;
            }

            // 进程睡眠
            Random rand = new Random();
            Integer sleepSeconds = rand.nextInt(2000 - 1000 + 1000) + 1000;
            System.out.println("睡眠 " + (sleepSeconds / 1000) + "秒钟");
            Thread.sleep(sleepSeconds);

            // 发送模拟数据给客户端
            DataOutputStream dos = new DataOutputStream(outStream);
            dos.writeInt(responseData.length());
            dos.writeBytes(responseData);
            System.out.println("++++++++++  发送数据给客户端  ++++++++++");
            System.out.println("发送消息长度 = " + responseData.length());
            System.out.println(responseData);
            break;
        }

        serverSocket.close();

        // byte[] bytes = new byte[1024];
        // InputStream in = client.getInputStream();
        // OutputStream out = client.getOutputStream();
        // Scanner scanner = new Scanner(System.in);

        // new Timer().schedule(new TimerTask() {
        // @Override
        // public void run() {
        // try {
        // out.write(scanner.next().getBytes(StandardCharsets.UTF_8));
        // } catch (IOException e) {
        // throw new RuntimeException(e);
        // }
        // }
        // }, 0, 500);

        // new Timer().schedule(new TimerTask() {
        // @Override
        // public void run() {
        // try {
        // int read = in.read(bytes);
        // String list = new String(bytes, 0, read, Charset.defaultCharset());
        // System.out.println("客户端: " + list);
        // } catch (IOException e) {
        // throw new RuntimeException(e);
        // }
        // }
        // }, 0, 500);

    }

}

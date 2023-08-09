package com.greatwall.mock.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @todo 封装NIO异步请求和响应处理，每次读写客户 SocketChannel 不阻塞线程
 * @author songhuabiao@greatwall.com.cn
 */
public class ClientSession {
    private String sessionId; // 代表用户的会话ID
    private int PACKET_HEAD_SIZE = 24;
    private ByteBuffer request_packet_head;
    private ByteBuffer request_packet_body;

    private ByteBuffer response_buffer;
    private int recv_bytes;
    private int send_bytes;
    private RequestState state;
    private BinaryPacket binaryPacket;

    public ClientSession() {
        this.generateSessionId();
        initBuffers();
    }

    public void initBuffers() {
        this.request_packet_head = ByteBuffer.allocate(PACKET_HEAD_SIZE);
        this.request_packet_body = null;
        this.response_buffer = null;
        this.recv_bytes = 0;
        this.send_bytes = 0;
        this.state = RequestState.RECV_HEAD;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public BinaryPacket getPacket() {
        return this.binaryPacket;
    }

    public void processClientResponse(SelectionKey key, Selector workerSelector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        switch (this.state) {
            case PREPARE_RESPONSE: {
                this.prepareResponseBody(key);
                this.state = RequestState.SEND_RESPONSE;
                break;
            }
            case SEND_RESPONSE: {
                // 巨坑！！！ 必须切换为读模式，否则 SocketChannel.write() 写入一直失败！发送字节一直为 0 ！！！！
                this.response_buffer.flip();
                int nBytes = socketChannel.write(this.response_buffer);
                Utils.printByteBuffer(this.response_buffer);
                if (nBytes > 0) {
                    this.send_bytes -= nBytes;
                }
                if (this.send_bytes <= 0) {
                    this.send_bytes = 0;
                    this.response_buffer = null;
                    this.state = RequestState.FINISH;
                } else {
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                    workerSelector.wakeup();
                }
                break;
            }
            case FINISH: {
                this.state = RequestState.RECV_HEAD;
                initBuffers();
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE); // 移除写入消息
                key.interestOps(key.interestOps() | SelectionKey.OP_READ); // 继续接受客户端消息
                workerSelector.wakeup();
                break;
            }
            case SOCKET_CLOSE:
            case SOCKET_RESET: {
                System.out.println("Oops! process client RESPONSE error! reset client socket!");
                this.state = RequestState.RECV_HEAD;
                key.cancel();
                break;
            }
            default:
                break;
        }
    }

    // 处理客户请求
    public void processClientRequest(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        switch (this.state) {
            case RECV_HEAD: {
                int nBytes = socketChannel.read(this.request_packet_head);
                if (nBytes == -1) { // 客户端连接已经关闭
                    this.state = RequestState.SOCKET_CLOSE;
                    System.out.println("客户端连接已经立即关闭");
                    break;
                } else {
                    if (nBytes == 0) {
                        key.interestOps(key.interestOps() | SelectionKey.OP_READ); // 继续接受包体
                        break;
                    }
                    this.recv_bytes += nBytes;
                    if (this.recv_bytes >= PACKET_HEAD_SIZE) {
                        this.state = RequestState.PARSE_HEAD; // 这里不能break，因为需要触发下一次读取
                    } else {
                        break;
                    }
                }
            }
            case PARSE_HEAD: {
                this.binaryPacket = this.parseRequestPacketHead(this.request_packet_head);
                this.request_packet_body = ByteBuffer.allocate(this.binaryPacket.body_size);
                this.recv_bytes = 0;
                this.state = RequestState.RECV_BODY;
                key.interestOps(key.interestOps() | SelectionKey.OP_READ); // 继续接受包体
                // 开始发送数据给客户，如果需要定时发送，则需要启用定时器
                break;
            }
            case RECV_BODY: {
                try {
                    int nBytes = socketChannel.read(this.request_packet_body);
                    if (nBytes == -1) {
                        this.state = RequestState.SOCKET_CLOSE;
                        System.out.println("客户端连接已经关闭");
                        break;
                    } else {
                        this.recv_bytes += nBytes;
                        if (this.recv_bytes >= this.binaryPacket.body_size) {
                            this.state = RequestState.PARSE_BODY;
                            this.recv_bytes = 0;
                        }
                    }
                } catch (SocketException e) {
                    System.out.println("[ERROR] " + e.getMessage());
                    this.state = RequestState.SOCKET_RESET;
                }
                break;
            }
            case PARSE_BODY: {
                this.parseRequstBody();
                this.request_packet_body = null;
                this.state = RequestState.PREPARE_RESPONSE;
                key.interestOps(key.interestOps() & ~SelectionKey.OP_READ); // 取消读事件
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE); // 增加写事件
                break;
            }
            case SOCKET_CLOSE:
            case SOCKET_RESET: {
                System.out.println("Oops! process client REQUEST error! reset client socket !");
                this.state = RequestState.RECV_HEAD;
                key.cancel();
                break;
            }
            default:
                break;
        }
    }

    // Files.writeString(Paths.get(file.toURI()), "My string to save");
    // -----------------------------------------------------------------
    // Files.writeString(Paths.get(file.toURI()),
    // "My string to save",
    // StandardCharsets.UTF_8,
    // StandardOpenOption.CREATE,
    // StandardOpenOption.TRUNCATE_EXISTING);

    public static String readJsonFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        String fileContent = new String(Files.readAllBytes(path));
        return fileContent;
    }

    public static String loadJsonFileInJarPackage(String filePath) {
        try {
            InputStreamReader jsonStream = new InputStreamReader(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(filePath));

            BufferedReader bufferReader = new BufferedReader(jsonStream);
            String line = "";
            String lines = "";
            while ((line = bufferReader.readLine()) != null) {
                lines = lines + line;
            }
            return lines;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "";
        }

    }

    // 解析包头
    public BinaryPacket parseRequestPacketHead(ByteBuffer byteBuffer) {
        byteBuffer.flip(); // 进入只读模式;
        byte[] head = new byte[PACKET_HEAD_SIZE];
        byteBuffer.get(head);
        BinaryReader reader = new BinaryReader(head);
        BinaryPacket packet = new BinaryPacket();
        packet.identifier = reader.readShort(ByteSequence.BIG_ENDIGAN);
        packet.body_size = reader.readInt(ByteSequence.BIG_ENDIGAN);
        packet.command = reader.readShort(ByteSequence.BIG_ENDIGAN);
        packet.timestamp = reader.readLong(ByteSequence.BIG_ENDIGAN);
        packet.serial_no = reader.readLong(ByteSequence.BIG_ENDIGAN);
        return packet;
    }

    // 解析请求包体
    public void parseRequstBody() {
        this.request_packet_body.flip(); // 开启读模式
        this.binaryPacket.body = StandardCharsets.UTF_8.decode(this.request_packet_body).toString();
        System.out.println("[client][" + this.sessionId + "]: " + this.binaryPacket.body);
    }

    // 将要发送给客户的文件内容转化为 ByteBuffer， 如果文件很大，需要考虑分块发送
    public void prepareResponseBody(SelectionKey key) throws IOException {
        String responseData = "";
        String jsonPath = "data/BatchServerPowerOn.json";
        Command cmd = Command.valueOf(this.binaryPacket.command);
        switch (cmd) {
            case BATCH_SERVER_POWER_ON:
                responseData = loadJsonFileInJarPackage(jsonPath);
                break;
            case BATCH_SERVER_POWER_OFF:
                responseData = loadJsonFileInJarPackage(jsonPath);
                break;
            case BATCH_SERVER_RESTART:
                responseData = loadJsonFileInJarPackage(jsonPath);
            default:
                break;
        }
        ByteBuffer dataBuffer = ByteBuffer.wrap(responseData.getBytes(StandardCharsets.UTF_8));
        ByteBufferWriter writer = new ByteBufferWriter(4 + dataBuffer.capacity());
        writer.writeInt(dataBuffer.capacity(), ByteSequence.BIG_ENDIGAN);
        writer.writeByteArray(dataBuffer.array());
        this.response_buffer = writer.getByteBuffer();
        this.send_bytes = this.response_buffer.capacity();
    }

    // 生成用户会话ID
    private void generateSessionId() {
        this.sessionId = UUID.randomUUID().toString();
    }

}

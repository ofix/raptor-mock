package com.greatwall.mock.server;

public class BinaryPacket {
    public short identifier; // 标识，默认为1
    public int body_size; // 包体长度
    public short command; // 命令代号
    public long timestamp; // 当前时间戳
    public long serial_no; // 流水号
    public String body; // 包体数据,JSON文本格式
}


package com.greatwall.mock.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BinaryWriter {
    private byte[] buffer;
    private int offset;
    private int size;

    public BinaryWriter(int size) {
        this.buffer = new byte[size];
        this.size = size;
        this.offset = 0;
    }

    public BinaryWriter(byte[] bytes) {
        this.buffer = bytes;
        this.size = bytes.length;
        this.offset = 0;
    }

    // 获取字节数组
    public byte[] getBytes() {
        return this.buffer;
    }

    // 获取字节大小
    public int getSize() {
        return this.size;
    }

    // 1个字节
    public BinaryWriter writeByte(byte data) {
        this.buffer[this.offset] = data;
        this.offset += 1;
        return this;
    }

    // 2个字节
    public BinaryWriter writeShort(short data, ByteSequence sequence) {
        if (sequence == ByteSequence.BIG_ENDIGAN) {
            this.buffer[this.offset] = (byte) ((data >> 8) & 0xFF);
            this.buffer[this.offset + 1] = (byte) (data & 0xFF);
        } else {
            this.buffer[this.offset] = (byte) (data & 0xFF);
            this.buffer[this.offset + 1] = (byte) ((data >> 8) & 0xFF);
        }

        this.offset += 2;
        return this;
    }

    // 4个字节
    public BinaryWriter writeInt(int data, ByteSequence sequence) {
        if (sequence == ByteSequence.BIG_ENDIGAN) {
            this.buffer[this.offset] = (byte) ((data >> 24) & 0xFF);
            this.buffer[this.offset + 1] = (byte) ((data >> 16) & 0xFF);
            this.buffer[this.offset + 2] = (byte) ((data >> 8) & 0xFF);
            this.buffer[this.offset + 3] = (byte) (data & 0xFF);
        } else {
            this.buffer[this.offset] = (byte) (data & 0xFF);
            this.buffer[this.offset + 1] = (byte) ((data >> 8) & 0xFF);
            this.buffer[this.offset + 2] = (byte) ((data >> 16) & 0xFF);
            this.buffer[this.offset + 3] = (byte) ((data >> 24) & 0xFF);
        }

        this.offset += 4;
        return this;
    }

    // 8个字节
    public BinaryWriter writeLong(long data, ByteSequence sequence) {
        if (sequence == ByteSequence.BIG_ENDIGAN) {
            this.buffer[this.offset] = (byte) ((data >> 56) & 0xFF);
            this.buffer[this.offset + 1] = (byte) ((data >> 48) & 0xFF);
            this.buffer[this.offset + 2] = (byte) ((data >> 40) & 0xFF);
            this.buffer[this.offset + 3] = (byte) ((data >> 32) & 0xFF);
            this.buffer[this.offset + 4] = (byte) ((data >> 24) & 0xFF);
            this.buffer[this.offset + 5] = (byte) ((data >> 16) & 0xFF);
            this.buffer[this.offset + 6] = (byte) ((data >> 8) & 0xFF);
            this.buffer[this.offset + 7] = (byte) (data & 0xFF);
        } else {
            this.buffer[this.offset] = (byte) (data & 0xFF);
            this.buffer[this.offset + 1] = (byte) ((data >> 8) & 0xFF);
            this.buffer[this.offset + 2] = (byte) ((data >> 16) & 0xFF);
            this.buffer[this.offset + 3] = (byte) ((data >> 24) & 0xFF);
            this.buffer[this.offset + 4] = (byte) ((data >> 32) & 0xFF);
            this.buffer[this.offset + 5] = (byte) ((data >> 40) & 0xFF);
            this.buffer[this.offset + 6] = (byte) ((data >> 48) & 0xFF);
            this.buffer[this.offset + 7] = (byte) ((data >> 56) & 0xFF);
        }

        this.offset += 8;
        return this;
    }

    // 4个字节
    public BinaryWriter writeFloat(float data, ByteSequence sequence) {
        int intBits = Float.floatToIntBits(data);
        if (sequence == ByteSequence.BIG_ENDIGAN) {
            this.buffer[this.offset] = (byte) ((intBits >> 24) & 0xFF);
            this.buffer[this.offset + 1] = (byte) ((intBits >> 16) & 0xFF);
            this.buffer[this.offset + 2] = (byte) ((intBits >> 8) & 0xFF);
            this.buffer[this.offset + 3] = (byte) (intBits & 0xFF);
        } else {
            this.buffer[this.offset] = (byte) (intBits & 0xFF);
            this.buffer[this.offset + 1] = (byte) ((intBits >> 8) & 0xFF);
            this.buffer[this.offset + 2] = (byte) ((intBits >> 16) & 0xFF);
            this.buffer[this.offset + 3] = (byte) ((intBits >> 24) & 0xFF);
        }

        this.offset += 4;
        return this;
    }

    // 8个字节
    public BinaryWriter writeDouble(double data, ByteSequence sequence) {
        Long intBits = Double.doubleToLongBits(data);
        if (sequence == ByteSequence.BIG_ENDIGAN) {
            this.buffer[this.offset] = (byte) ((intBits >> 56) & 0xFF);
            this.buffer[this.offset + 1] = (byte) ((intBits >> 48) & 0xFF);
            this.buffer[this.offset + 2] = (byte) ((intBits >> 40) & 0xFF);
            this.buffer[this.offset + 3] = (byte) ((intBits >> 32) & 0xFF);
            this.buffer[this.offset + 4] = (byte) ((intBits >> 24) & 0xFF);
            this.buffer[this.offset + 5] = (byte) ((intBits >> 16) & 0xFF);
            this.buffer[this.offset + 6] = (byte) ((intBits >> 8) & 0xFF);
            this.buffer[this.offset + 7] = (byte) (intBits & 0xFF);
        } else {
            this.buffer[this.offset] = (byte) (intBits & 0xFF);
            this.buffer[this.offset + 1] = (byte) ((intBits >> 8) & 0xFF);
            this.buffer[this.offset + 2] = (byte) ((intBits >> 16) & 0xFF);
            this.buffer[this.offset + 3] = (byte) ((intBits >> 24) & 0xFF);
            this.buffer[this.offset + 4] = (byte) ((intBits >> 32) & 0xFF);
            this.buffer[this.offset + 5] = (byte) ((intBits >> 40) & 0xFF);
            this.buffer[this.offset + 6] = (byte) ((intBits >> 48) & 0xFF);
            this.buffer[this.offset + 7] = (byte) ((intBits >> 56) & 0xFF);
        }

        this.offset += 8;
        return this;
    }

    public BinaryWriter writeBytes(byte[] bytes) {
        if ((this.buffer.length - this.offset) < bytes.length) {
            throw new RangeOverflowException("range overflow when call writeBytes!");
        }
        for (int i = 0; i < bytes.length; i++) {
            this.buffer[this.offset + i] = bytes[i];
        }
        this.offset += bytes.length;
        return this;
    }

    // 写入字符串
    public BinaryWriter writeString(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytes.length; i++) {
            this.buffer[this.offset + i] = bytes[i];
        }
        this.offset += bytes.length;
        return this;
    }

    // 日期转换成时间戳
    // public Long dateToTimestamp(Date date) {
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // String format = sdf.format(date);
    // Long time = null;
    // try {
    // time = sdf.parse(format).getTime();
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    // return time;
    // }
}
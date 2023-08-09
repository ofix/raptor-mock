package com.greatwall.mock.server;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteBufferReader {

    private ByteBuffer buffer;
    private int size;
    private int offset;

    public ByteBufferReader(ByteBuffer buffer) {
        this.buffer = buffer;
        this.size = buffer.limit();
        this.offset = 0;
    }

    // 获取字节大小
    public int getSize() {
        return this.size;
    }

    public ByteBuffer getByteBuffer() {
        return this.buffer;
    }

    public byte readByte() {
        this.buffer.clear();
        byte data = this.buffer.get(this.offset);
        this.offset += 1;
        return data;
    }

    public short readShort(ByteSequence byteSequence) {
        byte low = this.readByte();
        byte high = this.readByte();
        if (byteSequence == ByteSequence.BIG_ENDIGAN) {
            short data = (short) ((low << 8) | high);
            return data;
        } else {
            short data = (short) ((high << 8) | low);
            return data;
        }
    }

    public int readInt(ByteSequence byteSequence) {
        byte d1 = this.readByte();
        byte d2 = this.readByte();
        byte d3 = this.readByte();
        byte d4 = this.readByte();
        if (byteSequence == ByteSequence.BIG_ENDIGAN) {
            int data = (int) (((d1 & 0xFF) << 24) |
                    ((d2 & 0xFF) << 16) |
                    ((d3 & 0xFF) << 8) |
                    (d4 & 0xFF));
            return data;
        } else {
            int data = (int) ((d1 & 0xFF) |
                    ((d2 & 0xFF) << 8) |
                    ((d3 & 0xFF) << 16) |
                    ((d4 & 0xFF) << 24));
            return data;
        }
    }

    public long readLong(ByteSequence byteSequence) {
        byte d1 = this.readByte();
        byte d2 = this.readByte();
        byte d3 = this.readByte();
        byte d4 = this.readByte();
        byte d5 = this.readByte();
        byte d6 = this.readByte();
        byte d7 = this.readByte();
        byte d8 = this.readByte();
        if (byteSequence == ByteSequence.BIG_ENDIGAN) {
            long data = (long) (((d1 & 0xFFL) << 56) |
                    ((d2 & 0xFFL) << 48) |
                    ((d3 & 0xFFL) << 40) |
                    ((d4 & 0xFFL) << 32) |
                    ((d5 & 0xFFL) << 24) |
                    ((d6 & 0xFFL) << 16) |
                    ((d7 & 0xFFL) << 8) |
                    (d8 & 0xFF));
            return data;
        } else {
            long data = (long) ((d1 & 0xFFL) |
                    ((d2 & 0xFFL) << 8) |
                    ((d3 & 0xFFL) << 16) |
                    ((d4 & 0xFFL) << 24) |
                    ((d5 & 0xFFL) << 32) |
                    ((d6 & 0xFFL) << 40) |
                    ((d7 & 0xFFL) << 48) |
                    ((d8 & 0xFFL) << 56));
            return data;
        }
    }

    /**
     * @todo 支持 5,6,7 三种大小字节的数据读取
     * @param nBytes
     * @param byteSequence
     * @return
     */
    public long readNBytes(int nBytes, ByteSequence byteSequence) throws UnsupportedBytesException {
        if (nBytes <= 4 || nBytes >= 8) {
            throw new UnsupportedBytesException("parameter nBytes error, only 5,6,7 bytes supported");
        }
        byte[] readBytes = new byte[nBytes];
        for (int i = 0; i < nBytes; i++) {
            readBytes[i] = this.readByte();
        }
        long data = 0;
        if (byteSequence == ByteSequence.BIG_ENDIGAN) {
            for (int i = 0; i < nBytes; i++) {
                data = (long) ((readBytes[i] & 0xFFL) << (8 * (nBytes - 1 - i)) | data);
            }
            return data;
        } else {
            for (int i = 0; i < nBytes; i++) {
                data = (long) ((readBytes[i] & 0xFFL) << (8 * i) | data);
            }
            return data;
        }
    }

    public float readFloat(ByteSequence byteSequence) {
        int value = this.readInt(byteSequence);
        return Float.intBitsToFloat(value);
    }

    public double readDouble(ByteSequence byteSequence) {
        long value = this.readLong(byteSequence);
        return Double.longBitsToDouble(value);
    }

    public byte[] readByteArray(int offset, int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[offset + i] = this.buffer.get(offset);
        }
        return bytes;
    }

    public String readString() {
        int length = this.readInt(ByteSequence.BIG_ENDIGAN);
        // byte[] to string
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = this.buffer.get(this.offset + i);
        }
        // 以下内置方法不可用，因为 ByteBuffer 拷贝从offset开始，拷贝 length 个字节到目标数组 offset 起始位置
        // this.buffer.get(bytes, 0, length);
        Utils.printByteArray(bytes);
        String s = new String(bytes, StandardCharsets.UTF_8);
        return s;
    }
}

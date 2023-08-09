package com.greatwall.mock.server;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ByteBufferWriter {
    private ByteBuffer buffer;
    private int size;
    private int offset;

    public ByteBufferWriter(int size) {
        this.buffer = ByteBuffer.allocate(size);
        this.offset = 0;
        this.size = size;
    }

    // 获取字节数组
    public ByteBuffer getByteBuffer() {
        return this.buffer;
    }

    // 获取字节大小
    public int getSize() {
        return this.size;
    }

    // 1个字节
    public ByteBufferWriter writeByte(byte data) {
        this.buffer.put(data);
        this.offset += 1;
        return this;
    }

    // 2个字节
    public ByteBufferWriter writeShort(short data, ByteSequence sequence) {
        if (sequence == ByteSequence.BIG_ENDIGAN) {
            this.buffer.put((byte) ((data >> 24) & 0xFF));
            this.buffer.put((byte) ((data >> 16) & 0xFF));
        } else {
            this.buffer.put((byte) (data & 0xFF));
            this.buffer.put((byte) ((data >> 8) & 0xFF));
        }
        this.offset += 2;
        return this;
    }

    // 4个字节
    public ByteBufferWriter writeInt(int data, ByteSequence sequence) {
        if (sequence == ByteSequence.BIG_ENDIGAN) {
            this.buffer.put((byte) ((data >> 24) & 0xFF));
            this.buffer.put((byte) ((data >> 16) & 0xFF));
            this.buffer.put((byte) ((data >> 8) & 0xFF));
            this.buffer.put((byte) (data & 0xFF));
        } else {
            this.buffer.put((byte) (data & 0xFF));
            this.buffer.put((byte) ((data >> 8) & 0xFF));
            this.buffer.put((byte) ((data >> 16) & 0xFF));
            this.buffer.put((byte) ((data >> 24) & 0xFF));
        }

        this.offset += 4;
        return this;
    }

    // 8个字节
    public ByteBufferWriter writeLong(long data, ByteSequence sequence) {
        if (sequence == ByteSequence.BIG_ENDIGAN) {
            this.buffer.put((byte) ((data >> 56) & 0xFFL));
            this.buffer.put((byte) ((data >> 48) & 0xFFL));
            this.buffer.put((byte) ((data >> 40) & 0xFFL));
            this.buffer.put((byte) ((data >> 32) & 0xFFL));
            this.buffer.put((byte) ((data >> 24) & 0xFFL));
            this.buffer.put((byte) ((data >> 16) & 0xFFL));
            this.buffer.put((byte) ((data >> 8) & 0xFFL));
            this.buffer.put((byte) (data & 0xFFL));
        } else {
            this.buffer.put((byte) (data & 0xFFL));
            this.buffer.put((byte) ((data >> 8) & 0xFFL));
            this.buffer.put((byte) ((data >> 16) & 0xFFL));
            this.buffer.put((byte) ((data >> 24) & 0xFFL));
            this.buffer.put((byte) ((data >> 32) & 0xFFL));
            this.buffer.put((byte) ((data >> 40) & 0xFFL));
            this.buffer.put((byte) ((data >> 48) & 0xFFL));
            this.buffer.put((byte) ((data >> 56) & 0xFFL));
        }

        this.offset += 8;
        return this;
    }

    /**
     * @todo 支持5，6，7 三种字节的写入
     * @param nBytes
     * @param data
     * @param byteSequence
     * @return
     * @throws UnsupportedBytesException
     * @throws DataOverflowException
     */
    public ByteBufferWriter writeNBytes(int nBytes, long data, ByteSequence byteSequence)
            throws UnsupportedBytesException, DataOverflowException {
        if (nBytes <= 4 || nBytes >= 8) {
            throw new UnsupportedBytesException("parameter nBytes error, only 5,6,7 bytes supported");
        }
        if (data > (2 << (8 * nBytes))) {
            throw new DataOverflowException("data is overflow!");
        }
        if (byteSequence == ByteSequence.BIG_ENDIGAN) {
            for (int i = 0; i < nBytes; i++) {
                this.buffer.put((byte) ((data >> (8 * (nBytes - 1 - i))) & 0xFFL));
            }
        } else {
            for (int i = 0; i < nBytes; i++) {
                this.buffer.put((byte) ((data >> (8 * i)) & 0xFFL));
            }
        }

        this.offset += nBytes;
        return this;
    }

    // 4个字节
    public ByteBufferWriter writeFloat(float data, ByteSequence sequence) {
        int intBits = Float.floatToIntBits(data);
        return writeInt(intBits, sequence);
    }

    // 8个字节
    public ByteBufferWriter writeDouble(double data, ByteSequence sequence) {
        Long longBits = Double.doubleToLongBits(data);
        return this.writeLong(longBits, sequence);
    }

    // 写入字节
    public ByteBufferWriter writeByteArray(byte[] bytes) {
        this.buffer.put(bytes);
        return this;
    }

    // 写入字符串
    public ByteBufferWriter writeString(String data) {
        Charset charset = StandardCharsets.UTF_8;
        byte[] byteArrray = data.getBytes(charset);
        this.writeInt((int) byteArrray.length, ByteSequence.BIG_ENDIGAN);
        for (int i = 0; i < byteArrray.length; i++) {
            this.buffer.put(byteArrray[i]);
        }
        this.offset += byteArrray.length;
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


package com.greatwall.mock.server;

import java.nio.charset.StandardCharsets;

public class BinaryReader {
    private byte[] buffer;
    private int offset;
    private int size;

    public BinaryReader(byte[] bytes) {
        this.buffer = bytes;
        this.size = bytes.length;
        this.offset = 0;
    }

    // 获取字节大小
    public int getSize() {
        return this.size;
    }

    public byte readByte() {
        byte data = this.buffer[this.offset];
        this.offset += 1;
        return data;
    }

    /**
     * @todo 获取子数组大小
     * @param offset
     * @param size
     * @return
     */
    public byte[] slice(int offset, int size) {
        if (offset + size > this.size) {
            throw new RangeOverflowException("offset + size overflow buffer length");
        }
        byte[] buf = new byte[size];
        for (int i = 0; i < size; i++) {
            buf[i] = this.buffer[offset + i];
        }
        return buf;
    }

    public short readShort(ByteSequence byteSequence) {
        if (byteSequence == ByteSequence.BIG_ENDIGAN) {
            short data = (short) (((this.buffer[this.offset] & 0xFF) << 8) |
                    this.buffer[this.offset + 1]);
            this.offset += 2;
            return data;
        } else {
            short data = (short) ((this.buffer[this.offset] & 0xFF) |
                    ((this.buffer[this.offset + 1] & 0xFF) << 8));
            this.offset += 2;
            return data;
        }

    }

    public int readInt(ByteSequence byteSequence) {
        if (byteSequence == ByteSequence.BIG_ENDIGAN) {
            int data = (int) (((this.buffer[this.offset] & 0xFF) << 24) |
                    ((this.buffer[this.offset + 1] & 0xFF) << 16) |
                    ((this.buffer[this.offset + 2] & 0xFF) << 8) |
                    (this.buffer[this.offset + 3] & 0xFF));
            this.offset += 4;
            return data;
        } else {
            int data = (int) ((this.buffer[this.offset] & 0xFF) |
                    ((this.buffer[this.offset + 1] & 0xFF) << 8) |
                    ((this.buffer[this.offset + 2] & 0xFF) << 16) |
                    ((this.buffer[this.offset + 3] & 0xFF) << 24));
            this.offset += 4;
            return data;
        }
    }

    public long readLong(ByteSequence byteSequence) {
        if (byteSequence == ByteSequence.BIG_ENDIGAN) {
            long data = (long) (((this.buffer[this.offset] & 0xFFL) << 56) |
                    ((this.buffer[this.offset + 1] & 0xFFL) << 48) |
                    ((this.buffer[this.offset + 2] & 0xFFL) << 40) |
                    ((this.buffer[this.offset + 3] & 0xFFL) << 32) |
                    ((this.buffer[this.offset + 4] & 0xFFL) << 24) |
                    ((this.buffer[this.offset + 5] & 0xFFL) << 16) |
                    ((this.buffer[this.offset + 6] & 0xFFL) << 8) |
                    (this.buffer[this.offset + 7] & 0xFFL));
            this.offset += 8;
            return data;
        } else {
            long data = (long) ((this.buffer[this.offset] & 0xFFL) |
                    ((this.buffer[this.offset + 1] & 0xFFL) << 8) |
                    ((this.buffer[this.offset + 2] & 0xFFL) << 16) |
                    ((this.buffer[this.offset + 3] & 0xFFL) << 24) |
                    ((this.buffer[this.offset + 4] & 0xFFL) << 32) |
                    ((this.buffer[this.offset + 5] & 0xFFL) << 40) |
                    ((this.buffer[this.offset + 6] & 0xFFL) << 48) |
                    ((this.buffer[this.offset + 7] & 0xFFL)) << 56);
            this.offset += 8;
            return data;
        }
    }

    public String readString() {
        // byte[] to string
        String s = new String(this.buffer, StandardCharsets.UTF_8);
        return s;
    }

    // // convert file to byte[]
    // byte[] bytes = Files.readAllBytes(Paths.get("/path/image.png"));

    // // Java 8 - Base64 class, finally.

    // // encode, convert byte[] to base64 encoded string
    // String s = Base64.getEncoder().encodeToString(bytes);

    // System.out.println(s);

    // // decode, convert base64 encoded string back to byte[]
    // byte[] decode = Base64.getDecoder().decode(s);

    // // This Base64 encode decode string is still widely use in
    // // 1. email attachment
    // // 2. embed image files inside HTML or CSS

    // // Note
    // For text data byte[], try new String(bytes, StandardCharsets.UTF_8).
    // For binary data byte[], try Base64 encoding.

}
package com.greatwall.mock.server;

import java.io.IOException;

public class Application {

    public static void main(String[] args) {
        try {
            MockRaptorServer.run(args);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // ByteBufferWriter writer = new ByteBufferWriter(100);
        // writer.writeByte((byte) 26)
        // .writeDouble(77.015224D, ByteSequence.BIG_ENDIGAN)
        // .writeFloat(33.22F, ByteSequence.BIG_ENDIGAN)
        // .writeInt(3272, ByteSequence.BIG_ENDIGAN)
        // .writeLong(55202, ByteSequence.BIG_ENDIGAN)
        // .writeString("我是你的xiaos扫把");
        // Utils.printByteBuffer(writer.getByteBuffer());
        // ByteBufferReader reader = new ByteBufferReader(writer.getByteBuffer());
        // System.out.println("(1) " + reader.readByte());
        // System.out.println("(2) " + reader.readDouble(ByteSequence.BIG_ENDIGAN));
        // System.out.println("(3) " + reader.readFloat(ByteSequence.BIG_ENDIGAN));
        // System.out.println("(4) " + reader.readInt(ByteSequence.BIG_ENDIGAN));
        // System.out.println("(5) " + reader.readLong(ByteSequence.BIG_ENDIGAN));
        // System.out.println("(6) " + reader.readString());
    }
}

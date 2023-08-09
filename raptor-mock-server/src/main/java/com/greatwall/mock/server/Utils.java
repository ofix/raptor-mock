package com.greatwall.mock.server;

import java.nio.ByteBuffer;

public class Utils {
    public static int rand(int min, int max) {
        int num = min + (int) (Math.random() * (max - min + 1));
        return num;
    }

    public static void printByteArray(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        StringBuilder cb = new StringBuilder();
        System.out.println(" ");
        System.out.println("----------------------------------------------------------------------------------");
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0 && (i % 16 == 0)) {
                System.out.println(sb.toString() + " | " + cb.toString());
                sb.setLength(0);
                cb.setLength(0);
                String hex = Integer.toHexString((byte) bytes[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex + " ");
                char c = (char) (int) bytes[i];
                if (c < 32 || c >= 128) {
                    c = (char) 46;
                }
                cb.append(c + " ");
            } else {
                String hex = Integer.toHexString((byte) bytes[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex + " ");
                char c = (char) (int) bytes[i];
                if (c < 32 || c >= 128) {
                    c = (char) 46;
                }
                cb.append(c + " ");
            }
        }
        if (sb.length() > 0) {
            String blanks = padding((48 - sb.length()));
            System.out.println(sb.toString() + blanks + " | " + cb.toString());
            sb.delete(0, sb.length());
            cb.delete(0, cb.length());
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Byte[] array length: " + bytes.length);
        System.out.println(" ");
    }

    public static void printByteBuffer(ByteBuffer byteBuffer) {
        StringBuilder sb = new StringBuilder();
        StringBuilder cb = new StringBuilder();
        System.out.println(" ");
        System.out.println("----------------------------------------------------------------------------------");
        for (int i = 0; i < byteBuffer.limit(); i++) {
            if (i > 0 && (i % 16 == 0)) {
                System.out.println(sb.toString() + " | " + cb.toString());
                sb.setLength(0);
                cb.setLength(0);
                String hex = Integer.toHexString((byte) byteBuffer.get(i) & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex + " ");
                char c = (char) byteBuffer.get(i);
                if (c < 32 || c >= 128) {
                    c = (char) 46;
                }
                cb.append(c + " ");
            } else {
                String hex = Integer.toHexString((byte) byteBuffer.get(i) & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex + " ");
                char c = (char) byteBuffer.get(i);
                if (c < 32 || c >= 128) {
                    c = (char) 46;
                }
                cb.append(c + " ");
            }
        }
        if (sb.length() > 0) {
            String blanks = padding((48 - sb.length()));
            System.out.println(sb.toString() + blanks + " | " + cb.toString());
            sb.delete(0, sb.length());
            cb.delete(0, cb.length());
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("ByteBuffer capacity: " + byteBuffer.capacity() + " , limit: " + byteBuffer.limit());
        System.out.println(" ");
    }

    public static String padding(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}

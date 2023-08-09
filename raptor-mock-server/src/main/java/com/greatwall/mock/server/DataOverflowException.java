package com.greatwall.mock.server;

public class DataOverflowException extends RuntimeException {
    DataOverflowException(String msg) {
        super(msg);
    }
}

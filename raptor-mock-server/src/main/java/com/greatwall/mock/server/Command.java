package com.greatwall.mock.server;

public enum Command {
    BATCH_SERVER_POWER_ON(1), // 服务器批量开机
    BATCH_SERVER_POWER_OFF(2), // 服务器批量关机
    BATCH_SERVER_RESTART(3); // 服务器批量重启

    private int value = 0;

    private Command(int value) { // 必须是private的，否则编译错误
        this.value = value;
    }

    public static Command valueOf(int value) { // 手写的从int到enum的转换函数
        switch (value) {
            case 1:
                return BATCH_SERVER_POWER_ON;
            case 2:
                return BATCH_SERVER_POWER_OFF;
            case 3:
                return BATCH_SERVER_RESTART;
            default:
                return null;
        }
    }

    public int value() {
        return this.value;
    }

}

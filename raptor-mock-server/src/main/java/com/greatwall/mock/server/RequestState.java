package com.greatwall.mock.server;

public enum RequestState {
    RECV_HEAD,
    PARSE_HEAD,
    RECV_BODY,
    PARSE_BODY,
    PREPARE_RESPONSE,
    SEND_RESPONSE,
    FINISH,
    SOCKET_CLOSE, SOCKET_RESET
}

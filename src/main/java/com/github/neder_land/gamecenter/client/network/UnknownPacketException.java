package com.github.neder_land.gamecenter.client.network;

public class UnknownPacketException extends IllegalArgumentException {
    public UnknownPacketException() {
    }

    public UnknownPacketException(String s) {
        super(s);
    }

    public UnknownPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownPacketException(Throwable cause) {
        super(cause);
    }
}

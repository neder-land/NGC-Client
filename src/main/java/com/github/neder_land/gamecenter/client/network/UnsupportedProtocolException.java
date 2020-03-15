package com.github.neder_land.gamecenter.client.network;

public class UnsupportedProtocolException extends RuntimeException {
    public UnsupportedProtocolException(String s) {
        super(s);
    }

    public UnsupportedProtocolException(Throwable e) {
        super(e);
    }

    public UnsupportedProtocolException(String s, Throwable e) {
        super(s, e);
    }

    public UnsupportedProtocolException() {
        super();
    }

    public static class UnsupportedPacketException extends UnsupportedProtocolException {
        public UnsupportedPacketException() {
            super();
        }

        public UnsupportedPacketException(String s) {
            super(s);
        }

        public UnsupportedPacketException(Throwable t) {
            super(t);
        }

        public UnsupportedPacketException(String s, Throwable t) {
            super(s, t);
        }
    }
}

package com.github.neder_land.gamecenter.client.init;

public enum Protocol {
    legacy(0), v1(1);

    Protocol(int versionIn) {
        version = versionIn;
    }

    private final int version;

    public int getVersion() {
        return version;
    }

    public static boolean isSupported(int version) {
        return version == v1.version;
    }
}

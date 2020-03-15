package com.github.neder_land.gamecenter.client.init;

public enum Protocol {
    v1(1);

    Protocol(int versionIn) {
        version = versionIn;
    }

    private final int version;

    public int getVersion() {
        return version;
    }
}

package com.github.neder_land.gamecenter.client.api.mod;

public interface IModInfo extends Comparable<IModInfo> {
    String modid();

    String version();

    String name();

    String[] dependencies();

    String mainClass();

    boolean equals(Object o);

    int hashCode();

    String toString();
}

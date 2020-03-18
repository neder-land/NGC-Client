package com.github.neder_land.gamecenter.client.api.mod;

import neder_land.lib.Version;

import java.util.Map;

public interface IModInfo extends Comparable<IModInfo> {
    String modid();

    Version version();

    String name();

    Map<String, Version> dependencies();

    String mainClass();

    boolean equals(Object o);

    int hashCode();

    String toString();
}

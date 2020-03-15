package com.github.neder_land.gamecenter.client.util;

import com.github.neder_land.gamecenter.client.api.network.PacketId;
import com.github.neder_land.jww.packet.Packet;

public class PacketUtils {
    public static String getAction(PacketId id) {
        if (id == null) return "";
        return "game." + id.game() + "." + id.subType();
    }

    public static String getAction(Packet packet) {
        return getAction(packet.getClass());
    }

    public static String getAction(Class<? extends Packet> packet) {
        return getAction(packet.getAnnotation(PacketId.class));
    }
}

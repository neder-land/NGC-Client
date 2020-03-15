package com.github.neder_land.gamecenter.client.api.network;

import com.github.neder_land.jww.packet.Packet;

public abstract class GamePacket<T extends GameContent> extends Packet<T> {
    protected GamePacket(T content) {
        super(content);
    }
}

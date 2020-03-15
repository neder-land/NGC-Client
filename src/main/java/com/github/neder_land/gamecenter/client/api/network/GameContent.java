package com.github.neder_land.gamecenter.client.api.network;

import com.github.neder_land.jww.packet.PacketContent;

public abstract class GameContent extends PacketContent {
    protected GameContent(String game, String subtype) {
        super("game." + game + "." + subtype);
    }

}

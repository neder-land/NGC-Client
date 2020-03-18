package com.github.neder_land.gamecenter.client.mod.event;

import com.github.neder_land.gamecenter.client.logic.game.GCGames;

public class ClientInitializationEvent extends GenericEvent<GCGames> implements com.github.neder_land.gamecenter.client.api.mod.event.GenericEvent<GCGames>, com.github.neder_land.gamecenter.client.api.mod.event.Event {
    public ClientInitializationEvent(Object sender, GCGames wrapped) {
        super(sender, wrapped);
    }
}

package com.github.neder_land.gamecenter.client.mod.event;

import com.github.neder_land.gamecenter.client.api.mod.event.Event;

public class ClientShutdownEvent extends GenericEvent<Integer> implements com.github.neder_land.gamecenter.client.api.mod.event.GenericEvent<Integer>, Event {
    public ClientShutdownEvent(Object sender, Integer wrapped) {
        super(sender, wrapped);
    }
}

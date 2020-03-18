package com.github.neder_land.gamecenter.client.mod.event;

import com.github.neder_land.gamecenter.client.api.mod.event.Event;

public class IMCEvent extends GenericEvent<String> implements com.github.neder_land.gamecenter.client.api.mod.event.GenericEvent<String>, Event {
    public IMCEvent(Object sender, String wrapped) {
        super(sender, wrapped);
    }
}

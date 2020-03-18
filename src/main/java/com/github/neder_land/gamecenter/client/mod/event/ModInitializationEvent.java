package com.github.neder_land.gamecenter.client.mod.event;

import com.github.neder_land.gamecenter.client.mod.GCModEnv;

public class ModInitializationEvent extends GenericEvent<GCModEnv> implements com.github.neder_land.gamecenter.client.api.mod.event.ModInitializationEvent, com.github.neder_land.gamecenter.client.api.mod.event.GenericEvent<GCModEnv>, com.github.neder_land.gamecenter.client.api.mod.event.Event {
    public ModInitializationEvent(Object sender, GCModEnv wrapped) {
        super(sender, wrapped);
    }
}

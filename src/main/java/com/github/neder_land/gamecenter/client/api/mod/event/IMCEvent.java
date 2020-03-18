package com.github.neder_land.gamecenter.client.api.mod.event;

import com.github.neder_land.gamecenter.client.mod.ModInfo;

public interface IMCEvent<T> extends GenericEvent<T> {
    ModInfo sender();

    T message();
}

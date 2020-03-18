package com.github.neder_land.gamecenter.client.api.mod;

import com.github.neder_land.gamecenter.client.api.mod.event.Event;
import com.github.neder_land.gamecenter.client.mod.ModInfo;

public interface ModContainer<T> {
    <R extends Event> void fireEvent(R e);

    ModInfo getInfo();

    Class<T> getMainClass();

}

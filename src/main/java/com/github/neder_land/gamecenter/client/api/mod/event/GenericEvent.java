package com.github.neder_land.gamecenter.client.api.mod.event;

public interface GenericEvent<T> extends Event {
    T getWrapped();
}

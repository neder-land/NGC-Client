package com.github.neder_land.gamecenter.client.mod.event;

public abstract class Event implements com.github.neder_land.gamecenter.client.api.mod.event.Event {

    private final Object sender;

    public Event(Object sender) {
        this.sender = sender;
    }

    @Override
    public Object getSender() {
        return sender;
    }
}

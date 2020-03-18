package com.github.neder_land.gamecenter.client.mod.event;

public abstract class GenericEvent<T> extends Event implements com.github.neder_land.gamecenter.client.api.mod.event.GenericEvent<T> {

    final T wrapped;

    public GenericEvent(Object sender, T wrapped) {
        super(sender);
        this.wrapped = wrapped;
    }

    @Override
    public T getWrapped() {
        return wrapped;
    }
}

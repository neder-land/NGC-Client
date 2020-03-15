package com.github.neder_land.gamecenter.client.api.network;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public interface IPacketDispatcher {
    <T extends GamePacket<E>, E extends GameContent> void registerPacket(Class<T> packet, Class<E> content, String game, String subtype);

    <T extends GamePacket<E>, E extends GameContent> void unregisterPacket(Class<T> packet);

    <T extends GamePacket<E>, E extends GameContent> void registerListener(Class<T> packet, Consumer<T> listener);

    <T extends GamePacket<E>, E extends GameContent> void unregisterListener(Class<T> clz, Consumer<T> listener);

    <T extends GamePacket<E>, E extends GameContent> void sendCustomPacket(T packet, @Nullable Runnable callback);
}

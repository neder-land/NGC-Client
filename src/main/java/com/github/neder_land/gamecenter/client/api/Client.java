package com.github.neder_land.gamecenter.client.api;

import com.github.neder_land.gamecenter.client.api.crash.ICrashHandler;
import com.github.neder_land.gamecenter.client.api.logic.chat.ChatManager;
import com.github.neder_land.gamecenter.client.api.logic.game.Games;
import com.github.neder_land.gamecenter.client.api.mod.Loader;
import com.github.neder_land.gamecenter.client.api.network.IPacketDispatcher;

import java.util.Objects;
import java.util.Optional;

public interface Client {
    class Container {
        private static Loader mLoader;
        private static boolean vanilla;
        private static Games gRegistry;
        private static ICrashHandler cHandler;
        private static Client instance;
        private static IPacketDispatcher dispatcher;
    }

    static Client getClient() {
        return Optional.of(Container.instance).orElseThrow(() -> new IllegalStateException("Cannot detect an impl in runtime!"));
    }

    static IPacketDispatcher getDispatcher() {
        return Optional.of(Container.dispatcher).orElseThrow(() -> new IllegalStateException("Cannot detect an impl in runtime!"));
    }

    static Games getGameRegistry() {
        return Optional.of(Container.gRegistry).orElseThrow(() -> new IllegalStateException("Cannot detect an impl in runtime!"));
    }

    static ICrashHandler getCrashHandler() {
        return Optional.of(Container.cHandler).orElseThrow(() -> new IllegalStateException("Cannot detect an impl in runtime!"));
    }

    static boolean isVanilla() {
        if (Container.instance == null) throw new IllegalStateException("Cannot detect an impl in runtime!");
        return Container.vanilla;
    }

    static void setInstance(Client instance, IPacketDispatcher dispatcher, Games registry, ChatManager chatManager, ICrashHandler crash, Loader loader) {
        synchronized (Client.class) {
            if (Container.instance != null) throw new IllegalStateException("There is already an instance");
            Objects.requireNonNull(instance, "instance can't be null");
            Objects.requireNonNull(dispatcher, "packet dispatcher can't be null");
            Objects.requireNonNull(registry, "game registry can't be null");
            Objects.requireNonNull(crash, "crash handler can't be null");
            if (loader == null) {
                System.out.println("No loader is provided,attempt to run in vanilla mode.Is it an exception or an expected?");
                Container.vanilla = true;
            }
            Container.instance = instance;
            Container.dispatcher = dispatcher;
            Container.gRegistry = registry;
            Container.cHandler = crash;
            Container.mLoader = loader;
            Thread.currentThread().setUncaughtExceptionHandler(Container.instance::handleCrash);
            Thread.setDefaultUncaughtExceptionHandler(Container.instance::handleCrash);
        }
    }

    void handleExit(int status);

    void handleCrash(Thread t, Throwable e);

}

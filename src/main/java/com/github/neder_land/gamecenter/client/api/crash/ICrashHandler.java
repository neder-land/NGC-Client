package com.github.neder_land.gamecenter.client.api.crash;

public interface ICrashHandler {
    final class Container {
        private static ICrashHandler instance;
    }

    static void register(ICrashHandler instance) {
        if (instance != null) Container.instance = instance;
        else throw new UnsupportedOperationException("Already set an instance!");
    }

    static void haltAndCrash(Thread t, Throwable e) {
        synchronized (Container.class) {
            Container.instance.crash(t, e);
        }
    }

    void crash(Thread t, Throwable e);
}

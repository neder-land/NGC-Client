package com.github.neder_land.gamecenter.client.crash;

import com.github.neder_land.gamecenter.client.ClientHandler;
import com.github.neder_land.gamecenter.client.api.crash.ICrashHandler;
import com.github.neder_land.gamecenter.client.api.crash.ICrashReport;

import java.nio.file.Paths;
import java.util.Calendar;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class CrashHandler implements ICrashHandler {
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static Throwable crash;
    private static Thread crashed;
    private static final Thread CRASH_HANDLER = new Thread(() -> {
        lock.lock();
        try {
            while (true) {
                condition.awaitUninterruptibly();
                if (crash == null) continue;
                if (crash.getClass().equals(OutOfMemoryError.class)) {
                    ClientHandler.freeUpMemory();
                    continue;
                }
                break;
            }
            ICrashReport.make(crash, crashed).saveTo(Paths.get(String.format("crash_report-%s.log", Calendar.getInstance().toString()))).crash();
        } finally {
            lock.unlock();
        }
    }, "CrashHandler Thread");

    public synchronized void crash(Thread t, Throwable e) {
        crashed = t;
        crash = e;
        condition.signal();
        condition.awaitUninterruptibly();
    }
}

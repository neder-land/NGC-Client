package com.github.neder_land.gamecenter.client.api.crash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public interface ICrashReport {

    final class Container {
        private static Class<? extends ICrashReport> instance = DefaultCrashReport.class;

        private static final class DefaultCrashReport implements ICrashReport {
            String msg;

            @Override
            public void crash() {
                if (msg == null) return;
                System.exit(2);
            }

            @Override
            public void save(Path path) {
                if (msg == null) return;
                try {
                    FileChannel fc = FileChannel.open(path, StandardOpenOption.WRITE);
                    ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                    fc.write(buffer);
                    buffer.clear();
                    fc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void init(Throwable crash, Thread crashed) {
                Objects.requireNonNull(crash, "Exception can't be null");
                Objects.requireNonNull(crashed, "Thread can't be null");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                crash.printStackTrace(new PrintStream(baos));
                msg = "Exception in thread " + crashed + "\r\n";
                msg = msg.concat(baos.toString());
            }
        }
    }

    static ICrashReport make(Throwable crash, Thread crashed) {
        try {
            return Container.instance.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new Container.DefaultCrashReport();
    }

    void crash();

    void save(Path path);

    void init(Throwable crash, Thread crashed);

    default ICrashReport saveTo(Path saveAt) {
        save(saveAt);
        return this;
    }

    static void setInstance(Class<? extends ICrashReport> impl) {
        Objects.requireNonNull(impl, "Impl can't be null");
        if (Container.instance != null) Container.instance = impl;
    }
}

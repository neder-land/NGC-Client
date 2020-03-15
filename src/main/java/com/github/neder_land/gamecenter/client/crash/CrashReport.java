package com.github.neder_land.gamecenter.client.crash;

import com.github.neder_land.gamecenter.client.api.Client;
import com.github.neder_land.gamecenter.client.api.crash.ICrashReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import static com.github.neder_land.gamecenter.client.util.IOUtils.appendLine;

public class CrashReport implements ICrashReport {

    private StringBuilder sb = new StringBuilder();

    @Override
    public void crash() {
        Client.getClient().handleExit(2);
    }

    @Override
    public void save(Path path) {
        try {
            FileChannel fc = FileChannel.open(path, StandardOpenOption.WRITE);
            ByteBuffer buffer = ByteBuffer.allocate(128);
            ReadableByteChannel rbc = Channels.newChannel(new ByteArrayInputStream(sb.toString().getBytes()));
            buffer.clear();
            while (rbc.read(buffer) >= 0 || buffer.position() != 0) {
                buffer.flip();
                fc.write(buffer);
                buffer.compact();
            }
            buffer.clear();
            fc.close();
            rbc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(Throwable crash, Thread crashed) {
        sb.append("-------------GameClient Crash Report-------------").append("\r\n");
        appendLine(sb.append("Time:"), new Date().toString());
        appendLine(sb, "//Yep,this is ya bad thing lol");
        appendLine(sb.append("Thread:"), crashed.toString());
        appendLine(sb.append("Description:"), crash.getLocalizedMessage());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        crash.printStackTrace(new PrintWriter(baos));
        appendLine(sb, new String(baos.toByteArray()));
    }
}

package com.github.neder_land.gamecenter.client;

import com.github.neder_land.gamecenter.client.api.Client;
import com.github.neder_land.gamecenter.client.api.logic.chat.ChatManager;
import com.github.neder_land.gamecenter.client.api.network.GamePacket;
import com.github.neder_land.gamecenter.client.crash.CrashHandler;
import com.github.neder_land.gamecenter.client.crash.CrashReport;
import com.github.neder_land.gamecenter.client.logic.chat.GMChatManager;
import com.github.neder_land.gamecenter.client.logic.game.GCGames;
import com.github.neder_land.gamecenter.client.mod.GCModEnv;
import com.github.neder_land.gamecenter.client.mod.LoaderGC;
import com.github.neder_land.gamecenter.client.mod.ModInfo;
import com.github.neder_land.gamecenter.client.mod.event.ClientInitializationEvent;
import com.github.neder_land.gamecenter.client.mod.event.ClientShutdownEvent;
import com.github.neder_land.gamecenter.client.mod.event.ModInitializationEvent;
import com.github.neder_land.gamecenter.client.network.PacketDispatcher;
import com.github.neder_land.gamecenter.client.util.VersionAdapter;
import com.github.neder_land.gamecenter.client.view.ClientWindow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import neder_land.lib.Version;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ClientHandler implements Client {

    //private static final boolean exit = false;
    private static final Path ROOT = Paths.get(".");
    public static final ClientHandler INSTANCE = new ClientHandler();
    public static final PacketDispatcher DISPATCHER = new PacketDispatcher();
    public static final GCGames GAMES = new GCGames();
    @Deprecated
    public static final ChatManager CHAT_MANAGER = new GMChatManager();
    public static final CrashHandler CRASH_HANDLER = new CrashHandler();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(GamePacket.class, DISPATCHER) //Out custom packet adapter!
            .registerTypeAdapter(Version.class, new VersionAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .enableComplexMapKeySerialization()
            .generateNonExecutableJson() //Safety first
            .create();
    public static final LoaderGC MODLOADER = new LoaderGC();
    private ClientHandler() {
        StackTraceElement[] element = Thread.currentThread().getStackTrace();
        if (!element[1].getClassName().contentEquals(ClientHandler.class.getName()))
            throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws IOException {
        Client.setInstance(INSTANCE, DISPATCHER, GAMES, CHAT_MANAGER, CRASH_HANDLER, MODLOADER);
        List<ModInfo> mis = MODLOADER.detect(INSTANCE);
        MODLOADER.verifyModCompatibility(mis);
        GCModEnv me = MODLOADER.buildModEnvironment(mis);
        me.post(new ModInitializationEvent(INSTANCE, me));
        me.post(new ClientInitializationEvent(INSTANCE, GAMES));
        ClientWindow.get().setVisible(true);
    }

    public static void freeUpMemory() {
        System.gc();
    }

    public void handleExit(int status) {
        /*Thread[] threads = Thread.getAllStackTraces().keySet().toArray(new Thread[0]);
        for(Thread thread:threads){
            if(thread!=Thread.currentThread()&&!thread.isDaemon()){
                thread.setUncaughtExceptionHandler((t,e)->{});
                thread.suspend();
            }
        }
        try {
            Field f = ClientHandler.class.getDeclaredField("exit");
            f.setAccessible(true);
            Field modifier = Field.class.getDeclaredField("modifiers");
            modifier.setAccessible(true);
            modifier.set(f,f.getModifiers() & ~Modifier.FINAL);
            f.set(null,true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }*/
        //Fuck AccessController one day
        if (MODLOADER.getEnvironment() != null)
            MODLOADER.getEnvironment().post(new ClientShutdownEvent(INSTANCE, status));
        System.exit(status);
    }

    public void handleCrash(Thread t, Throwable e) {
        System.err.println("handleCrash trapped");
        CrashReport report = new CrashReport();
        report.init(e, t);
        report.saveTo(ROOT.resolve("crash.log")).crash();
    }
}

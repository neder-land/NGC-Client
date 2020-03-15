package com.github.neder_land.gamecenter.client;

import com.github.neder_land.gamecenter.client.api.Client;
import com.github.neder_land.gamecenter.client.api.logic.chat.ChatManager;
import com.github.neder_land.gamecenter.client.api.network.GamePacket;
import com.github.neder_land.gamecenter.client.crash.CrashHandler;
import com.github.neder_land.gamecenter.client.crash.CrashReport;
import com.github.neder_land.gamecenter.client.logic.game.GCGames;
import com.github.neder_land.gamecenter.client.mod.LoaderGC;
import com.github.neder_land.gamecenter.client.network.PacketDispatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler implements Client {

    //private static final boolean exit = false;
    private static final Path ROOT = Paths.get(".");
    public static final ClientHandler INSTANCE = new ClientHandler();
    public static final PacketDispatcher DISPATCHER = new PacketDispatcher();
    public static final GCGames GAMES = new GCGames();
    public static final ChatManager CHAT_MANAGER = new ChatManager() {
    };
    public static final CrashHandler CRASH_HANDLER = new CrashHandler();
    public static final LoaderGC MODLOADER = new LoaderGC();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(GamePacket.class, DISPATCHER) //Out custom packet adapter!
            .setPrettyPrinting()
            .serializeNulls()
            .enableComplexMapKeySerialization()
            .generateNonExecutableJson() //Safety first
            .create();

    private ClientHandler() {
        StackTraceElement[] element = Thread.currentThread().getStackTrace();
        if (!element[1].getClassName().contentEquals(ClientHandler.class.getName()))
            throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        Client.setInstance(INSTANCE, DISPATCHER, GAMES, CHAT_MANAGER, CRASH_HANDLER, MODLOADER);

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
        System.exit(status);
    }

    public void handleCrash(Thread t, Throwable e) {
        System.err.println("handleCrash trapped");
        CrashReport report = new CrashReport();
        report.init(e, t);
        report.saveTo(ROOT.resolve("crash.log")).crash();
    }
}

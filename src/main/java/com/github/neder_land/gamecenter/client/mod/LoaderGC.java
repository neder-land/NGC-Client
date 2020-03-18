package com.github.neder_land.gamecenter.client.mod;

import com.github.neder_land.gamecenter.client.ClientHandler;
import com.github.neder_land.gamecenter.client.api.Client;
import com.github.neder_land.gamecenter.client.api.mod.IModInfo;
import com.github.neder_land.gamecenter.client.api.mod.Loader;
import com.github.neder_land.gamecenter.client.util.CollectionUtils;
import neder_land.lib.Version;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LoaderGC implements Loader<ModInfo> {
    public LoaderGC() {
    }

    private boolean activated;
    private List<ModInfo> modlist;
    private static final Path modpath = Paths.get(".").resolve(System.getProperty("com.github.neder_land.gamecenter.client.ModFolder", "mods"));
    private GCModEnv environment;
    @Override
    public List<ModInfo> detect(Client ch) throws IOException {
        if (!(ch instanceof ClientHandler))
            throw new LoaderException("ClientHandler can't be null!", new NullPointerException("ClientHandler is null"));
        if (!activated) {
            Stream<Path> files = Files.walk(modpath);
            try {
                modlist = files.filter(path -> path.endsWith(".jar")).map(path -> {
                    try (JarFile jf = new JarFile(path.toFile(), true)) {
                        ModInfo info = identifyAndLoadMod(jf, path.toUri().toURL());
                        if (info.equals(ModInfo.INVALID)) {
                            System.out.println("Jarfile " + jf.getName() + " is not a valid mod file.Injecting into class path...");
                            return null;
                        } else System.out.println("Discovered mod " + info);
                        return info;
                    } catch (IOException e) {
                        System.err.println("Cannot read jar " + path.toAbsolutePath() + " ,skipping");
                        e.printStackTrace();
                        return ModInfo.INVALID;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
            } finally {
                activated = true;
                System.out.println("GC Mod loader has identified " + modlist.size() + " mods to load.");
            }
        } else
            throw new LoaderException("Loader has already been initialized!", new IllegalStateException("LoaderGC initialized more than once"));
        return modlist;
    }

    @Override
    public GCModEnv buildModEnvironment(List<ModInfo> unique) {
        if (!activated) throw new LoaderException("Haven't initialized yet!", new IllegalStateException());
        return (environment = new GCModEnv(unique));
    }

    @Override
    public void verifyModCompatibility(List<ModInfo> info) {
        List<ModInfo> unique = CollectionUtils.unique(info);
        List<ModInfo> duplicate = new LinkedList<>(info);
        duplicate.removeAll(unique);
        for (IModInfo duplication : duplicate) {
            System.err.println("Found duplicate mod:" + duplication.toString());
        }
        for (IModInfo uniqued : unique) {
            for (Map.Entry<String, Version> dependency : uniqued.dependencies().entrySet()) {
                if (unique.stream()
                        .filter(mi -> mi.modid().equals(dependency.getKey()))
                        .filter(mi -> mi.version().equals(dependency.getValue()))
                        .count() < 1)
                    throw new LoaderException(String.format("%s requires %s@%s,found nothing", uniqued, dependency.getKey(), dependency.getValue()));
            }
        }
        info.clear();
        info.addAll(unique);
    }

    private static ModInfo identifyAndLoadMod(JarFile jf, URL url) {
        JarEntry entry = jf.getJarEntry("mod.json");
        if (entry == null) return ModInfo.INVALID;
        try (Reader r = new InputStreamReader(jf.getInputStream(entry))) {
            ModInfo mi = ClientHandler.GSON.fromJson(r, ModInfo.class);
            mi.setModJar(jf, url);
            return mi;
        } catch (IOException ignored) {
            return ModInfo.INVALID;
        }
    }

    public GCModEnv getEnvironment() {
        return environment;
    }
}

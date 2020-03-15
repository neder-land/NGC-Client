package com.github.neder_land.gamecenter.client.mod;

import com.github.neder_land.gamecenter.client.api.mod.Mod;
import com.github.neder_land.gamecenter.client.api.mod.ModEnv;
import com.google.common.collect.Maps;
import com.google.common.reflect.Invokable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class GCModEnv implements ModEnv {

    private final List<ModInfo> infos;
    private final Map<String, Invokable<ModEnv, Void>> methods = Maps.newHashMap();
    private final Map<String, Invokable<Void, Void>> noarg = Maps.newHashMap();
    private final URLClassLoader loader;

    private static class InfoAndClass {
        @Nonnull
        private final ModInfo info;
        @Nullable
        private final Class<?> mainClz;

        public InfoAndClass(@Nonnull ModInfo info, @Nullable Class<?> mainClz) {
            this.info = info;
            this.mainClz = mainClz;
        }
    }

    public GCModEnv(List<ModInfo> infos) {
        this.infos = infos;
        loader = URLClassLoader.newInstance(
                infos.stream().map(ModInfo::getLocation).distinct().toArray(URL[]::new), Thread.currentThread().getContextClassLoader());
        Stream<InfoAndClass> stream = infos.stream().map(s -> {
            Class<?> modClass;
            try {
                modClass = loader.loadClass(s.mainClass());
                System.out.println("Loaded mod class " + modClass);
            } catch (Exception e) {
                System.err.println(String.format("Could not load mod %s:Unable to load class %s", s, s.mainClass()));
                e.printStackTrace();
                modClass = null;
            }
            return new InfoAndClass(s, modClass);
        }).filter(s -> s.mainClz != null).onClose(() -> System.out.println("Loaded all mods."));
        stream.forEach(iac -> {
            assert iac.mainClz != null;
            Method[] methods = Arrays.stream(iac.mainClz.getMethods())
                    .filter(m -> m.getAnnotation(Mod.InitializerMethod.class) != null)
                    .toArray(Method[]::new);
            if (methods.length == 0) {
                System.out.format("Mod %s seemed to have no initializer method!", iac.info.modid()).println();
                return;
            }
            if (methods.length > 1) {
                System.err.format("Mod %s has multiple initializer method!THIS WILL NOT WORK!Disabling mod...", iac.info.modid()).println();
                infos.remove(iac.info);
            }
            Method initializer = methods[0];
            if (initializer.getParameterCount() > 1)
                System.err.format("Mod %s has initializer method with %d args!THIS WILL NOT WORK!Disabling mod...", iac.info.modid(), initializer.getParameterCount()).println();

        });
        stream.close();
    }

    public class GCModComm implements ModCommunication {
        @Override
        public <T> void sendMessage(String modid, T message) {

        }

        @Override
        public <T> void sendRuntimeMessage(String modid, T message) {

        }
    }

    /**
     * Get the communicator with your modid
     *
     * @param modid your modid
     * @return A mod communicator
     */
    @Override
    public ModCommunication getModComms(String modid) {
        return null;
    }

    /**
     * Call initialize
     */
    @Override
    public void initialize() {

    }
}

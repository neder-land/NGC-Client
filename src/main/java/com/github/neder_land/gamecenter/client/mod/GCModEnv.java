package com.github.neder_land.gamecenter.client.mod;

import com.github.neder_land.gamecenter.client.api.mod.ModEnv;
import com.github.neder_land.gamecenter.client.api.mod.event.Event;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class GCModEnv implements ModEnv {

    private final List<ModInfo> infos;
    private final URLClassLoader loader;
    private final Set<ModContainer> containers = Sets.newHashSet();

    private static class InfoAndClass<T> {
        @Nonnull
        final ModInfo info;
        @Nullable
        final Class<T> mainClz;

        public InfoAndClass(@Nonnull ModInfo info, @Nullable Class<T> mainClz) {
            this.info = info;
            this.mainClz = mainClz;
        }
    }

    public GCModEnv(List<ModInfo> infos) {
        this.infos = infos;
        loader = URLClassLoader.newInstance(
                infos.stream().map(ModInfo::getLocation).distinct().toArray(URL[]::new), Thread.currentThread().getContextClassLoader());
        Stream<? extends InfoAndClass<?>> stream = infos.stream().map(s -> {
            Class<?> modClass;
            try {
                modClass = loader.loadClass(s.mainClass());
                System.out.println("Loaded mod class " + modClass);
            } catch (Exception e) {
                System.err.println(String.format("Could not load mod %s:Unable to load class %s", s, s.mainClass()));
                e.printStackTrace();
                modClass = null;
            }
            return new InfoAndClass<>(s, modClass);
        }).filter(s -> s.mainClz != null).onClose(() -> System.out.println("Loaded all mods."));
        stream.forEach(iac -> {
            containers.add(new ModContainer<>(iac.info, iac.mainClz, getClass()));
        });
        stream.close();
    }

    @Override
    public <T extends Event> void post(T event) {
        containers.forEach(mc -> mc.fireEvent(event));
    }

    @Override
    public <T extends Event> void fireEvent(String modid, T event) {
        containers.stream().filter(mc -> mc.getInfo().modid().equals(modid)).findAny().ifPresent(mc -> mc.fireEvent(event));
    }

    @Override
    public Optional<com.github.neder_land.gamecenter.client.api.mod.ModContainer> getMod(String modid) {
        return infos.stream()
                .filter(i -> i.modid().equals(modid))
                .map(i -> (com.github.neder_land.gamecenter.client.api.mod.ModContainer) containers.stream().filter(mc -> mc.getInfo().equals(i)).findAny().get())
                .findAny();
    }

}

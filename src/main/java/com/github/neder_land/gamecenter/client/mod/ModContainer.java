package com.github.neder_land.gamecenter.client.mod;

import com.github.neder_land.gamecenter.client.api.mod.Mod;
import com.github.neder_land.gamecenter.client.api.mod.event.Event;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({"UnstableApiUsage", "unused", "FieldCanBeLocal"})
public class ModContainer<T> implements com.github.neder_land.gamecenter.client.api.mod.ModContainer<T> {
    private final ModInfo info;
    private final Class<T> mainClass;
    private final T instance;
    private boolean initialized = false;
    private final Map<Class<? extends Event>, Invokable<T, Void>> handlers = Maps.newHashMap();
    private static final Field MODIFIERS;

    static {
        try {
            MODIFIERS = Field.class.getDeclaredField("modifiers");
            MODIFIERS.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public ModContainer(ModInfo info, Class<T> mainClass, Class<? extends GCModEnv> envClz) {
        Objects.requireNonNull(info, "Info must not be null");
        Objects.requireNonNull(mainClass, "class must not be null");
        this.info = info;
        this.mainClass = mainClass;
        Method[] methods = Arrays.stream(mainClass.getMethods())
                .filter(m -> m.getAnnotation(Mod.EventHandler.class) != null)
                .toArray(Method[]::new);
        for (Method m : methods) {
            Invokable<T, Void> inv = TypeToken.of(mainClass).method(m).returning(Void.class);
            ImmutableList<Parameter> parameters = inv.getParameters();
            if (parameters.size() == 1) {
                TypeToken<?> tt = parameters.get(0).getType();
                if (tt.isSubtypeOf(Event.class)) {
                    handlers.putIfAbsent(tt.getRawType().asSubclass(Event.class), inv);
                    System.out.format("Injected event handler %s for mod %s", inv.getName(), info.modid()).println();
                }
            }
        }
        try {
            instance = mainClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new LoaderException(String.format("Cannot access constructor of Mod %s(%s@%s)", info.name(), info.modid(), info.version()), e);
        } catch (InstantiationException e) {
            throw new LoaderException(String.format("Unable to instantiate Mod %s(%s@%s)", info.name(), info.modid(), info.version()));
        }
        Field[] fs = mainClass.getDeclaredFields();
        Arrays.stream(fs)
                .filter(f -> f.getAnnotation(Mod.Instance.class) != null)
                .forEach(f -> {
                    f.setAccessible(true);
                    if (!Modifier.isStatic(f.getModifiers())) return;
                    if (Modifier.isFinal(f.getModifiers())) {
                        try {
                            AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
                                Field f2 = Field.class.getDeclaredField("modifiers");
                                f2.setAccessible(true);
                                f2.set(f, f.getModifiers() & ~Modifier.FINAL);
                                return null;
                            });
                        } catch (PrivilegedActionException e) {
                            throw new LoaderException(String.format("Unable to inject instance for Mod %s(%s@%s)", info.name(), info.modid(), info.version()));
                        }
                    }
                    try {
                        f.set(null, instance);
                    } catch (IllegalAccessException e) {
                        throw new LoaderException(String.format("Unable to inject instance for Mod %s(%s@%s)", info.name(), info.modid(), info.version()));
                    }
                });
    }

    public <R extends Event> void fireEvent(R e) {
        Objects.requireNonNull(e, "event must not be null");
        Optional.ofNullable(handlers.get(e.getClass())).ifPresent(
                inv -> {
                    try {
                        inv.invoke(instance, e);
                    } catch (InvocationTargetException ex) {
                        throw new LoaderException.ModCrash(String.format("Caught exception from Mod %s(%s)@%s during firing event.", info.name(), info.modid(), info.version()), ex);
                    } catch (IllegalAccessException ex) {
                        throw new LoaderException(String.format("Failed to invoke handler of Mod %s(%s@%s)", info.name(), info.modid(), info.version()), ex);
                    }
                }
        );
    }

    @Override
    public ModInfo getInfo() {
        return info;
    }

    @Override
    public Class<T> getMainClass() {
        return mainClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModContainer)) return false;
        ModContainer<?> that = (ModContainer<?>) o;
        return info.equals(that.info) &&
                mainClass.equals(that.mainClass) &&
                instance.equals(that.instance) &&
                handlers.equals(that.handlers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, mainClass, instance, handlers);
    }
}

package com.github.neder_land.gamecenter.client.api.mod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use to mark as a main class of a mod.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {
    String modid();

    String version();

    String name();

    String[] dependencies() default {};

    /**
     * Annotate on a public method to mark as a handler handling event its argument refers to
     * Must be in @Mod.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface EventHandler {
    }

    /**
     * Annotate on a field(Suggest you use private field) to mark it as your mod's instance.
     * Must be in @Mod
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Instance {
    }
}

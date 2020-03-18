package com.github.neder_land.gamecenter.client.api.mod;

import com.github.neder_land.gamecenter.client.api.mod.event.Event;

import java.util.Optional;

/**
 * An environment containing all the mods,used to dispatch event etc.
 */
public interface ModEnv {
    /**
     * Post an event to all mods.
     * @param event Event to fire.
     */
    <T extends Event> void post(T event);

    /**
     * Fire an event to a known Mod.
     * @param modid The mod's identifier
     * @param event Event to fire.
     */
    <T extends Event> void fireEvent(String modid, T event);

    /**
     * Get the mod.
     * @param modid The mod's identifier
     * @return A optional containing the mod's info.
     */
    Optional<ModContainer> getMod(String modid);
}

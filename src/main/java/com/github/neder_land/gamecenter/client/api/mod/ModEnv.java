package com.github.neder_land.gamecenter.client.api.mod;

/**
 * An environment containing all the mods,used to dispatch event etc.
 */
public interface ModEnv {
    /**
     * Utility to make comm between mods.
     */
    interface ModCommunication {
        <T> void sendMessage(String modid, T message);

        <T> void sendRuntimeMessage(String modid, T message);
    }

    /**
     * Get the communicator with your modid
     *
     * @param modid your modid
     * @return A mod communicator
     */
    ModCommunication getModComms(String modid);

    /**
     * Call initialize
     */
    void initialize();
}

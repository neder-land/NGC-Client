package com.github.neder_land.gamecenter.client.api.logic.game;

import javax.annotation.Nonnull;
import javax.swing.*;

/**
 * Method annotated with {@code PacketListener} will be auto registered.
 * Update UI with these methods.
 */
public interface Game {

    /**
     * Default game UI layout.
     *
     * @return A default game ui with basic layout.
     */
    @Nonnull
    JPanel getDefaultUI();

    /**
     * Invoked when exit the game.(Exit the room or the program crashes)
     */
    void onExit();
}

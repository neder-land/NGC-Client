package com.github.neder_land.gamecenter.client.api.logic.game;

import javax.swing.*;

public interface Games {
    void registerGame(String gameId, Game game);

    JPanel getGameUI(String gameId);
}

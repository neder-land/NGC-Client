package com.github.neder_land.gamecenter.client.logic.game;

import com.github.neder_land.gamecenter.client.ClientHandler;
import com.github.neder_land.gamecenter.client.api.logic.game.Game;
import com.github.neder_land.gamecenter.client.api.logic.game.Games;
import com.github.neder_land.gamecenter.client.api.network.GamePacket;
import com.github.neder_land.gamecenter.client.api.network.PacketListener;
import com.google.common.collect.Maps;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class GCGames implements Games {

    Map<String, Game> games = Maps.newHashMap();
    private static final JPanel ERROR_PANE = new JPanel();

    static {
        JLabel label = new JLabel();
        label.setText("Missing GUI");
        ERROR_PANE.add(label);
    }


    @Override
    public void registerGame(String gameId, Game game) {
        Class<? extends Game> gameClz = game.getClass();
        Arrays.stream(gameClz.getDeclaredMethods())
                .filter(m -> m.getAnnotation(PacketListener.class) != null)
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> GamePacket.class.isAssignableFrom(m.getParameterTypes()[0]))
                .forEach(m -> ClientHandler.DISPATCHER.registerListener(m.getParameterTypes()[0].asSubclass(GamePacket.class), p -> {
                    try {
                        m.invoke(game, p);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }));
    }

    @Override
    public JPanel getGameUI(String gameId) {
        return Optional.ofNullable(games.get(gameId)).map(Game::getDefaultUI).orElse(ERROR_PANE);
    }
}

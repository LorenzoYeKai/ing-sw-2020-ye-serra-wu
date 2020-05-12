package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.game.gods.God;

import java.util.List;

public interface PlayerData {
    String getName();
    boolean isDefeated();
    List<? extends WorkerData> getAllWorkers();
    List<? extends WorkerData> getAvailableWorkers();
    God getGod();
}

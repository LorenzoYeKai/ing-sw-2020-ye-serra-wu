package it.polimi.ingsw.models.game;

public interface WorkerData {
    PlayerData getPlayer();
    Space getInitialSpace();
    World getWorld();
    Space getFirstBuild();
}

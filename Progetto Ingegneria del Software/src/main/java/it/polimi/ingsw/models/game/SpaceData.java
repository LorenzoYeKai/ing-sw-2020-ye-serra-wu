package it.polimi.ingsw.models.game;

public interface SpaceData {
    int getLevel();
    WorkerData getWorker();
    boolean isOccupiedByDome();
    boolean isOccupied();
    int getX();
    int getY();
}

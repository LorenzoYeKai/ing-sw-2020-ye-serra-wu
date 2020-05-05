package it.polimi.ingsw.models.game;

public interface SpaceData {
    int getLevel();
    WorkerData getWorkerData();
    boolean isOccupiedByDome();
    boolean isOccupied();
    int getX();
    int getY();
}

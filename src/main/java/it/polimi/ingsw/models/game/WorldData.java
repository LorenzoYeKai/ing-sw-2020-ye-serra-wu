package it.polimi.ingsw.models.game;

public interface WorldData {
    Space getSpaces(int x, int y);
    SpaceData[][] getSpaceData();
}

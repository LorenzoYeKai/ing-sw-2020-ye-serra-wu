package it.polimi.ingsw.models.game;

import it.polimi.ingsw.Notifiable;

public class World {
    Space[][] spaces = new Space[5][5];

    /**
     * Creates an empty world
     */
    public World(Notifiable<SpaceData> onSpaceChanged) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                spaces[i][j] = new Space(onSpaceChanged, i, j);
            }
        }
    }

    public Space getSpaces(int x, int y) {
        return spaces[y][x];
    }


}



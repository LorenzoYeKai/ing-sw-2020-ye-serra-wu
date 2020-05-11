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

    public World(World copy){
        this.spaces = new Space[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                this.spaces[i][j] = new Space(copy.spaces[i][j]);
            }
        }
    }

    public Space getSpaces(int x, int y) {
        return spaces[y][x];
    }


    /**
     * returns null if not in the world
     */
    public Space pushSpace(Space firstSpace, Space secondSpace){
        int x = -1;
        int y = -1;
        if(firstSpace.getX() == secondSpace.getX()){
            x = firstSpace.getX();
            if(firstSpace.getY() < secondSpace.getY()){
                y = firstSpace.getY() + 2;
            }
            if(firstSpace.getY() > secondSpace.getY()){
                y = firstSpace.getY() - 2;
            }
        }
        if(firstSpace.getY() == secondSpace.getY()){
            y = firstSpace.getY();
            if(firstSpace.getX() < secondSpace.getX()){
                x = firstSpace.getX() + 2;
            }
            if(firstSpace.getX() > secondSpace.getX()){
                x = firstSpace.getX() - 2;
            }
        }
        if(firstSpace.getX() < secondSpace.getX() && firstSpace.getY() < secondSpace.getY()){
            x = firstSpace.getX() + 2;
            y = firstSpace.getY() + 2;
        }
        if(firstSpace.getX() > secondSpace.getX() && firstSpace.getY() > secondSpace.getY()){
            x = firstSpace.getX() - 2;
            y = firstSpace.getY() - 2;
        }
        if(firstSpace.getX() < secondSpace.getX() && firstSpace.getY() > secondSpace.getY()){
            x = firstSpace.getX() + 2;
            y = firstSpace.getY() - 2;
        }
        if(firstSpace.getX() > secondSpace.getX() && firstSpace.getY() < secondSpace.getY()){
            x = firstSpace.getX() - 2;
            y = firstSpace.getY() + 2;
        }
        if(y > -1 && y < 5 && x > -1 && x < 5){
            return this.spaces[x][y];
        }

        return null;
    }

}



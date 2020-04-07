package it.polimi.ingsw.models.game;

public class World {
    Space[][] spaces = new Space[5][5];

    /**
     * Creates an empty world
     */
    public World () {
        for(int i=0;i<5;i++) {
            for(int j=0;j<5;j++) {
                spaces[i][j]=new Space(i,j);
            }
        }
    }

    public Space getSpaces(int x, int y){
            return spaces [x][y] ;
        }

    /**
     * Checks if the given coordinates are of a space within the world
     */
    public boolean isInWorld(int x, int y)
    {
        return y > -1 && y < 5 && x > -1 && x < 5;
    }

    /**
     * Calculates the level difference between 2 spaced
     * Returns negative if moving up
     * Returns positive if moving down
     * Returns 0 if moving in same level
     */
    public  int levelDifference(int currentX, int currentY, int x, int y){
        return getSpaces(currentX, currentY).getLevel() - getSpaces(x, y).getLevel();
    }

    /**
     * Checks if a space is a neighbor of another space
     */
    public boolean isNeighbor(int currentX, int currentY, int x, int y)
    {
        for (int i=currentY-1; i<currentY+2; i=i+1){
            for (int j=currentX-1; j<currentX+2; j=j+1){
                if(i==y && j==x)
                    return true;
            }
        }
        return false;
    }
}



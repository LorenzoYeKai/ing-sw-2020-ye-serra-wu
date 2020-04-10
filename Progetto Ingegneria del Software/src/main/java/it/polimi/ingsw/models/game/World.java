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


}



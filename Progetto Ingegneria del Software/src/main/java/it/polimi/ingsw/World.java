package it.polimi.ingsw;

public class World {

        public World () {
            World =new Space [5][5];
            for(int i=0;i<5;i++) {
                for(int j=0;j<5;j++) {
                    World[i][j]=new Space(i,j);
                }
            }

    }
}

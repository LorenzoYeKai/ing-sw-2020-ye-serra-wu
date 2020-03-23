package it.polimi.ingsw;

public class World {
        static Space[][] spaces = new Space[5][5];

        public World () {
            for(int i=0;i<5;i++) {
                for(int j=0;j<5;j++) {
                    spaces[i][j]=new Space(i,j);
                }
            }
        }
        public static Space getSpaces(int x, int y){
            return spaces [x][y] ;
        }

        public static boolean isInWorld(int x, int y)
        {
            return y > -1 && y < 5 && x > -1 && x < 5;
        }

        public static boolean isNext(int currentX, int currentY, int x, int y)
        {
            for (int i=currentY-1; i<currentY+2; i=i+1){
                for (int j=currentX-1; j<currentX+2; j=j+1){
                    if(i==y && j==x)
                        return true;
                }
            }
            return false;
        }

        public static int levelDifference(int currentX, int currentY, int x, int y){
            return spaces[currentX][currentY].getLevel()-spaces[x][y].getLevel();
        }

        public static boolean canMoveThere(int currentX,int currentY,int x,int y){
            int k = levelDifference(currentX, currentY, x, y);
            return ( isNext(currentX,currentY, x, y) && isInWorld(x,y) && k>-2 && !getSpaces(x,y).isOccupied() );
        }

        public static boolean canBuildThere(int currentX, int currentY, int x, int y) {
            return (isInWorld(x,y) && isNext(currentX,currentY,x,y) && !spaces[x][y].isOccupied());
    }
}



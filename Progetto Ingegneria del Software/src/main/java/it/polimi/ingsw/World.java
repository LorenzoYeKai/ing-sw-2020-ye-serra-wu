package it.polimi.ingsw;

public class World {
        Space[][] spaces = new Space[5][5];

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

        public boolean isInWorld(int x, int y)
        {
            return y > -1 && y < 5 && x > -1 && x < 5;
        }

        public boolean isNext(int currentX, int currentY, int x, int y)
        {
            for (int i=currentY-1; i<currentY+2; i=i+1){
                for (int j=currentX-1; j<currentX+2; j=j+1){
                    if(i==y && j==x)
                        return true;
                }
            }
            return false;
        }

        public  int levelDifference(int currentX, int currentY, int x, int y){
            return spaces[currentX][currentY].getLevel()-spaces[x][y].getLevel();
        }

        public boolean canMoveThere(int currentX,int currentY,int x,int y){
            int k = levelDifference(currentX, currentY, x, y);
            return ( isNext(currentX,currentY, x, y) && isInWorld(x,y) && k>-2 && !getSpaces(x,y).isOccupied() );
        }

        public boolean canBuildThere(int currentX, int currentY, int x, int y) {
            return (isInWorld(x,y) && isNext(currentX,currentY,x,y) && !spaces[x][y].isOccupied());
        }

        public boolean canMove(int currentX,int currentY){
            for (int i=currentY-1; i<currentY+2; i=i+1){
                for (int j=currentX-1; j<currentX+2; j=j+1){
                    if(canMoveThere(currentX,currentY,i,j) && j!=currentX && i!=currentY)
                        return true;
                }
            }
            return false;
        }
}



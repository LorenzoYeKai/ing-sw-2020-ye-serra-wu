package it.polimi.ingsw;

public class Rule {

    private World world;

    public Rule(World world){
        this.world = world;
    }

    public boolean isInWorld(int x, int y)
    {
        return y > -1 && y < 5 && x > -1 && x < 5;
    }

    public  int levelDifference(int currentX, int currentY, int x, int y){
        return world.getSpaces(currentX, currentY).getLevel() - world.getSpaces(x, y).getLevel();
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

    public boolean canMoveThere(int currentX, int currentY, int x, int y){
        int k = levelDifference(currentX, currentY, x, y);
        return ( isNext(currentX,currentY, x, y) && isInWorld(x,y) && k>-2 && !this.getWorld().getSpaces(x,y).isOccupied() );
    }

    public boolean canBuildThere(int currentX, int currentY, int x, int y) {
        return (isInWorld(x,y) && isNext(currentX,currentY,x,y) && !this.getWorld().getSpaces(x, y).isOccupied());
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

    public World getWorld(){
        return this.world;
    }
}

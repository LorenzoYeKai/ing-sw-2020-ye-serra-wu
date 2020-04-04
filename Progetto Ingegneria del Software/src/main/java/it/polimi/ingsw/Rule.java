package it.polimi.ingsw;

abstract public class Rule {

    private World world;

    public Rule(World world){
        this.world = world;
    }



    public boolean canMoveThere(int currentX, int currentY, int x, int y){
        return ( this.getWorld().isNext(currentX,currentY, x, y) &&
                this.getWorld().isInWorld(x,y) &&
                this.getWorld().levelDifference(currentX, currentY, x, y) > -2 &&
                !this.getWorld().getSpaces(x,y).isOccupied() );
    }

    public boolean canBuildThere(int currentX, int currentY, int x, int y) {
        return (this.getWorld().isInWorld(x,y) &&
                this.getWorld().isNext(currentX,currentY,x,y) && !
                this.getWorld().getSpaces(x, y).isOccupied());
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

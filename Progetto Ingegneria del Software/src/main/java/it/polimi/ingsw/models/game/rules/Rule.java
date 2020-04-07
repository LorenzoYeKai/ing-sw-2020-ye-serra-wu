package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.World;

/**
 * Contains all the default rules implementation
 */
abstract public class Rule {

    private World world;

    public Rule(World world){
        this.world = world;
    }

    /**
     * Checks if a worker can move in a particular space
     * @param x : destination x
     * @param y : destination y
     *
     */
    public boolean canMoveThere(int currentX, int currentY, int x, int y){
        return ( this.getWorld().isNeighbor(currentX,currentY, x, y) &&
                this.getWorld().isInWorld(x,y) &&
                this.getWorld().levelDifference(currentX, currentY, x, y) > -2 &&
                !this.getWorld().getSpaces(x,y).isOccupied() );
    }

    /**
     * Checks if a worker can build in a particular space
     * @param x : destination x
     * @param y : destination y
     */
    public boolean canBuildThere(int currentX, int currentY, int x, int y) {
        return (this.getWorld().isInWorld(x,y) &&
                this.getWorld().isNeighbor(currentX,currentY,x,y) && !
                this.getWorld().getSpaces(x, y).isOccupied());
    }

    /**
     * Checks if a worker can move
     */
    public boolean canMove(int currentX,int currentY){
        for (int i=currentY-1; i<currentY+2; i=i+1){
            for (int j=currentX-1; j<currentX+2; j=j+1){
                if(canMoveThere(currentX,currentY,i,j) && j!=currentX && i!=currentY)
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks if the move performed by a worker leads to victory
     * @param x : destination x
     * @param y : destination y
     */
    public boolean winCondition(int currentX, int currentY, int x, int y){
        return world.getSpaces(x, y).getLevel() == 3 && world.getSpaces(currentX, currentY).getLevel() != 3;
    }

    public World getWorld(){
        return this.world;
    }
}

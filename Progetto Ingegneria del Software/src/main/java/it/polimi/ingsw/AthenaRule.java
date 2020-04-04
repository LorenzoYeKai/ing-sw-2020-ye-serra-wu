package it.polimi.ingsw;

public class AthenaRule extends Rule {

    public AthenaRule(World world) {
        super(world);
    }

    @Override
    public boolean canMoveThere(int currentX, int currentY, int x, int y){
        return ( this.getWorld().isNext(currentX,currentY, x, y) &&
                this.getWorld().isInWorld(x,y) &&
                this.getWorld().levelDifference(currentX, currentY, x, y) > -1 &&
                !this.getWorld().getSpaces(x,y).isOccupied() );
    }

}

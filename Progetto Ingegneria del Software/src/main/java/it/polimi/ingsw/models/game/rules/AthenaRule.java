package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.World;

/**
 * Rules for Athena's passive power
 */
public class AthenaRule extends Rule {

    public AthenaRule(World world) {
        super(world);
    }

    /**
     * Overrides the levelDifference(currentX, currentY, x, y) > -2 condition into levelDifference(currentX, currentY, x, y) > -1
     */
    @Override
    public boolean canMoveThere(int currentX, int currentY, int x, int y){
        return ( this.getWorld().isNeighbor(currentX,currentY, x, y) &&
                this.getWorld().isInWorld(x,y) &&
                this.getWorld().levelDifference(currentX, currentY, x, y) > -1 &&
                !this.getWorld().getSpaces(x,y).isOccupied() );
    }

}

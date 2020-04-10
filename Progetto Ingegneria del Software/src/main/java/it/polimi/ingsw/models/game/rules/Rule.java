package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;

/**
 * Contains all the default rules implementation
 */
public class Rule {

    private World world;

    public Rule(World world){
        this.world = world;
    }

    /**
     * Alcuni originalSpace sono passati solo per avere uniformità di parametri per la HashMap
     * NON sono sicuro si possa o sia poco elegamte farlo ma è la migliore soluzione per ora
     */
    public static boolean defaultIsNeighbor(Space originalSpace, Space targetSpace){
        return originalSpace.isNeighbor(targetSpace);
    }

    public static boolean defaultLevelDifference(Space originalSpace, Space targetSpace){
        return originalSpace.levelDifference(targetSpace) > -2;
    }

    public static boolean defaultIsOccupiedByWorker(Space originalSpace, Space targetSpace){
        return originalSpace.isInWorld() && targetSpace.isOccupiedByWorker();
    }

    public static boolean defaultIsOccupiedByDome(Space originalSpace, Space targetSpace){
        return originalSpace.isInWorld() && targetSpace.isOccupiedByDome();
    }

    public static boolean defaultIsInWorld(Space originalSpace, Space targetSpace){
        return originalSpace.isInWorld() && targetSpace.isInWorld();
    }

    /**
     * Checks if a worker can build in a particular space
     */

    public static boolean defaultIsOccupied(Space originalSpace, Space targetSpace){
        return originalSpace.isInWorld() && targetSpace.isOccupied();
    }


    /**
     * Checks if the move performed by a worker leads to victory
     */
    public static boolean defaultWinCondition(Space originalSpace, Space targetSpace){
        return originalSpace.getLevel() == 3 && targetSpace.getLevel() != 3;
    }

    public World getWorld(){
        return this.world;
    }
}

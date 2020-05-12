package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;

/**
 * Contains all the default rules implementation
 */
public class DefaultRule {

    private World world;

    public DefaultRule(World world){
        this.world = world;
    }

    /**
     * Alcuni currentSpace sono passati solo per avere uniformità di parametri per la HashMap
     * NON sono sicuro si possa o sia poco elegamte farlo ma è la migliore soluzione per ora
     */
    public static boolean defaultIsNeighbor(Space currentSpace, Space targetSpace){
        return currentSpace.isNeighbor(targetSpace);
    }

    public static boolean defaultLevelDifference(Space currentSpace, Space targetSpace){
        return currentSpace.levelDifference(targetSpace) > -2;
    }

    public static boolean defaultIsFreeFromWorker(Space currentSpace, Space targetSpace){
        return currentSpace.isInWorld() && !targetSpace.isOccupiedByWorker();
    }

    public static boolean defaultIsFreeFromDome(Space currentSpace, Space targetSpace){
        return currentSpace.isInWorld() && !targetSpace.isOccupiedByDome();
    }

    public static boolean defaultIsInWorld(Space currentSpace, Space targetSpace){
        return currentSpace.isInWorld() && targetSpace.isInWorld();
    }

    /**
     * Checks if a worker can build in a particular space
     */

    public static boolean defaultIsFree(Space currentSpace, Space targetSpace){
        return currentSpace.isInWorld() && !targetSpace.isOccupied();
    }

    public static boolean defaultCanBuildDomeLevel(Space currentSpace, Space targetSpace){
        return currentSpace.isInWorld() && targetSpace.getLevel() == 3;
    }

    public static boolean defaultBuildLevelLimit(Space currentSpace, Space targetSpace){
        return currentSpace.isInWorld() && targetSpace.getLevel() < 3;
    }


    /**
     * Checks if the move performed by a worker leads to victory
     */
    public static boolean defaultWinCondition(Space currentSpace, Space targetSpace){
        return currentSpace.getLevel() != 3 && targetSpace.getLevel() == 3;
    }

    public World getWorld(){
        return this.world;
    }
}

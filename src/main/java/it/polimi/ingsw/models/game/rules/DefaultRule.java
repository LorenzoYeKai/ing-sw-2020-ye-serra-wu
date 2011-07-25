package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.World;

import java.io.Serializable;

/**
 * Contains all the default rules implementation
 */
public class DefaultRule implements Serializable {

    /**
     * Alcuni currentSpaceData sono passati solo per avere uniformità di parametri per la HashMap
     * NON sono sicuro si possa o sia poco elegamte farlo ma è la migliore soluzione per ora
     */
    public static boolean defaultIsNeighbor(Worker worker, Space target){
        return worker.getCurrentSpace().getPosition().isNeighbor(target.getPosition());
    }

    public static boolean defaultLevelDifference(Worker worker, Space target){
        return worker.getCurrentSpace().levelDifference(target) > -2;
    }

    public static boolean defaultIsFreeFromWorker(Worker worker, Space target){
        return !target.isOccupiedByWorker();
    }

    public static boolean defaultIsFreeFromDome(Worker worker, Space target){
        return !target.isOccupiedByDome();
    }

    // TODO: this should be assumed to be always true
    @Deprecated
    public static boolean defaultIsInWorld(Worker worker, Space targetSpace){
        return World.isInWorld(targetSpace.getPosition());
    }

    /**
     * Checks if can build in a particular space because it's free
     * @param worker the worker
     * @param target the target space
     * @return true if worker can build on the target space because it's free
     */
    public static boolean defaultIsFree(Worker worker, Space target){
        return !target.isOccupied();
    }

    /**
     * Check if can build dome by checking {@link Space#getLevel()}.
     * @param worker the worker
     * @param target the target space
     * @return true if the worker can build dome on the target space
     */
    public static boolean defaultCanBuildDomeLevel(Worker worker, Space target){
        return target.getLevel() == 3;
    }

    /**
     * Limit the maximum number of levels
     * @param worker the worker
     * @param target the target space
     * @return true if worker can build on the target space because of current level
     */
    public static boolean defaultBuildLevelLimit(Worker worker, Space target){
        return target.getLevel() < 3;
    }


    /**
     * Checks if the move performed by a worker leads to victory
     */
    public static boolean defaultWinCondition(Worker worker, Space target){
        return worker.getCurrentSpace().getLevel() != 3 && target.getLevel() == 3;
    }

}

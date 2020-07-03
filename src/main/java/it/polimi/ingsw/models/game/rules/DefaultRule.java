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
     *
     * set of all the methods that manage the basic rules of the game
     *
     * @param worker the worker which wants to move
     * @param target the destination space
     * @return
     */


    public static boolean defaultIsNeighbor(Worker worker, Space target) {
        return worker.getCurrentSpace().getPosition().isNeighbor(target.getPosition());
    }

    public static boolean defaultLevelDifference(Worker worker, Space target) {
        return worker.getCurrentSpace().levelDifference(target) > -2;
    }

    public static boolean defaultIsFreeFromWorker(Worker worker, Space target) {
        return !target.isOccupiedByWorker();
    }

    public static boolean defaultIsFreeFromDome(Worker worker, Space target) {
        return !target.isOccupiedByDome();
    }

    /**
     * Checks if worker can move because it will be the first action.
     * Santorini's rules requires (unless overridden by a God Power) that you
     * must always move then build. Which means
     *
     * @param worker the worker
     * @param target the target space.
     * @return true (can move) if this will be the first action of worker.
     * Otherwise false.
     */
    public static boolean defaultMoveWillBeFirstAction(Worker worker, Space target) {
        return worker.getWorld().getNumberOfSavedPreviousWorlds() == 0;
    }

    /* Build rules */

    /**
     * Checks if worker can build because it has moved.
     * Santorini's rules requires (unless overridden by a God Power) that you
     * must always move then build. Which means you can build only after move.
     *
     * @param worker the worker
     * @param target the target space
     * @return true (can build) if the worker has moved. Otherwise false.
     */
    public static boolean defaultBuildAfterMove(Worker worker, Space target) {
        if (worker.getWorld().getNumberOfSavedPreviousWorlds() == 0) {
            return false;
        }
        return worker.isLastActionMove();
    }

    /**
     * Checks if can build in a particular space because it's free
     *
     * @param worker the worker
     * @param target the target space
     * @return true if worker can build on the target space because it's free
     */
    public static boolean defaultIsFree(Worker worker, Space target) {
        return !target.isOccupied();
    }

    /**
     * Check if can build dome by checking {@link Space#getLevel()}.
     *
     * @param worker the worker
     * @param target the target space
     * @return true if the worker can build dome on the target space
     */
    public static boolean defaultCanBuildDomeLevel(Worker worker, Space target) {
        return target.getLevel() == 3;
    }

    /**
     * Limit the maximum number of levels
     *
     * @param worker the worker
     * @param target the target space
     * @return true if worker can build on the target space because of current level
     */
    public static boolean defaultBuildLevelLimit(Worker worker, Space target) {
        return target.getLevel() < 3;
    }


    /**
     * Checks if the move performed by a worker leads to victory
     */
    public static boolean defaultWinCondition(Worker worker, Space target) {
        return worker.getCurrentSpace().getLevel() != 3 && target.getLevel() == 3;
    }

}

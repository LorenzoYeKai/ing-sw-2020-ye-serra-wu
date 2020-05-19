package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;

import java.io.Serializable;

/**
 * Rules for Athena's passive power
 * Will be renamed as godPower
 */
public class GodPower extends DefaultRule implements Serializable {

    public GodPower(World world) {
        super(world);
    }

    /**
     * Adds the currentSpace.levelDifference(targetSpace) > -1 condition to the rules
     * Used by athena and prometheus
     * Tested
     */
    public static boolean cannotMoveUpPower(Space currentSpace, Space targetSpace){
        return currentSpace.levelDifference(targetSpace) > 0;
    }

    /**
     * Used only for the second movement of Artemis which cannot move back to the initial position
     * Tested
     */
    public static boolean artemisPower(Space currentSpace, Space targetSpace){
        return currentSpace.getWorker().previousSpace() != targetSpace;
    }

    /**
     * Needs to be tested again
     */
    public static boolean panPower(Space currentSpace, Space targetSpace){
        return currentSpace.levelDifference(targetSpace) > 1;
    }

    /**
     * Tested
     */
    public static boolean demeterPower(Space currentSpace, Space targetSpace) {
        if(currentSpace.getWorker().hasBuilt()) {
            return targetSpace != currentSpace.getWorker().previousBuild();
        }
        else return true;
    }

    /**
     * Tested
     */
    public static boolean hephaestusPower(Space currentSpace, Space targetSpace) {
        if(currentSpace.getWorker().hasBuilt()) {
            return (targetSpace == currentSpace.getWorker().previousBuild());
        }
        else return true;
    }

    /**
     * Tested
     */
    public static boolean minotaurPower(Space currentSpace, Space targetSpace) {
        if(targetSpace.isOccupiedByWorker()){
            return !currentSpace.getWorker().getPlayer().equals(targetSpace.getWorker().getPlayer()) &&
                    currentSpace.getWorker().getWorld().pushSpace(currentSpace, targetSpace) != null &&
                    !currentSpace.getWorker().getWorld().pushSpace(currentSpace, targetSpace).isOccupied();
        }
        return true;
    }

    /**
     * Tested
     */
    public static boolean apolloPower(Space currentSpace, Space targetSpace){
        if(targetSpace.isOccupiedByWorker()){
            return !currentSpace.getWorker().getPlayer().equals(targetSpace.getWorker().getPlayer());
        }
        return true;
    }
}

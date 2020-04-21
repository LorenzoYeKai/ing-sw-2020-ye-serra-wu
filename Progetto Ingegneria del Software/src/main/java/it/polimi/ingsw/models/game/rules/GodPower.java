package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;

import java.util.ArrayList;

/**
 * Rules for Athena's passive power
 * Will be renamed as godPower
 */
public class GodPower extends DefaultRule {

    public GodPower(World world) {
        super(world);
    }

    /**
     * Adds the currentSpace.levelDifference(targetSpace) > -1 condition to the rules
     * Tested
     */
    public static boolean athenaPower(Space currentSpace, Space targetSpace){
        return currentSpace.levelDifference(targetSpace) > 0;
    }

    /**
     * Used only for the second movement of Artemis which cannot move back to the initial position
     * Tested
     */
    public static boolean artemisPower(Space currentSpace, Space targetSpace){
        return currentSpace.getWorker().getInitialSpace() != targetSpace;
    }

    public static boolean panPower(Space currentSpace, Space targetSpace){
        return currentSpace.levelDifference(targetSpace) > 1;
    }
    public static boolean atlasPower(Space currentSpace, Space targetSpace){
        return !targetSpace.isOccupied();
    }
    public static boolean demeterPower(Space currentSpace, Space targetSpace) {
        return targetSpace != currentSpace.getWorker().getFirstBuild();
    }
    public static boolean hephaestusPower(Space currentSpace, Space targetSpace) {
        return (targetSpace == currentSpace.getWorker().getFirstBuild() && targetSpace.getLevel() != 2);
    }
    public static boolean minotaurPower(Space currentSpace, Space targetSpace) {
    }
    public static boolean prometheusPower(Space currentSpace, Space targetSpace) {
        return currentSpace.getWorker().getInitialSpace()==currentSpace;
    }
}

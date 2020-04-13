package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;

/**
 * Rules for Athena's passive power
 * Will be renamed as godPower
 */
public class GodPower extends DefaultRule {

    public GodPower(World world) {
        super(world);
    }

    /**
     * Adds the originalSpace.levelDifference(targetSpace) > -1 condition to the rules
     * Tested
     */
    public static boolean athenaPower(Space originalSpace, Space targetSpace){
        return originalSpace.levelDifference(targetSpace) > -1;
    }

    /**
     * Used only for the second movement of Artemis which cannot move back to the initial position
     * Tested
     */
    public static boolean artemisPower(Space originalSpace, Space targetSpace){
        return originalSpace.getWorker().getInitialSpace() != targetSpace;
    }

}

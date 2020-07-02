package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.*;

import java.io.Serializable;

/**
 * Rules for Athena's passive power
 * Will be renamed as godPower
 */
public class GodPower extends DefaultRule implements Serializable {

    public GodPower(World world) {
        super();
    }

    /**
     * Adds the currentSpace.levelDifference(targetSpace) > -1 condition to the rules
     * Used by athena and prometheus
     * Tested
     */
    // TODO: Remove
    @Deprecated
    public static boolean cannotMoveUpPower(Worker worker, Space target){
        return worker.getCurrentSpace().levelDifference(target) > 0;
    }
}

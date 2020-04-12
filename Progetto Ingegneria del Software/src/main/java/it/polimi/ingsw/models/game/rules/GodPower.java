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
     * Overrides the levelDifference(currentX, currentY, x, y) > -2 condition into levelDifference(currentX, currentY, x, y) > -1
     */
    public static boolean athenaPower(Space originalSpace, Space targetSpace){
        return originalSpace.levelDifference(targetSpace) > -1;
    }

    /**
     * Servirebbero 3 parametri per impedire che torni nella posizione di origine
     * PENSARE
     */
    public static boolean artemisPower(Space originalSpace, Space targetSpace){
        throw new UnsupportedOperationException("Not implemented yet");
    }

}

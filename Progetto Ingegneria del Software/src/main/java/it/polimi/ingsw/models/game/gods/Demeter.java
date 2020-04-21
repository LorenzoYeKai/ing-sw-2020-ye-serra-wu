package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.GodPower;

/**
 * Not implemented yet
 */
public class Demeter extends God {


    @Override
    public void activateGodPower(Worker worker) {
        worker.getRules().addBuildRules("demeterPower", GodPower::demeterPower);
    }

    @Override
    public void deactivateGodPower(Worker worker) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

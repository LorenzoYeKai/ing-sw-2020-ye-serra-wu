package it.polimi.ingsw.models.game.gods;


import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.GodPower;

/**
 * Not implemented yet
 */

public class Atlas extends God {


    @Override
    public void activateGodPower(Worker worker) {
        worker.getRules().addBuildDomeRules("atlasPower", GodPower::atlasPower);
    }

    @Override
    public void deactivateGodPower(Worker worker) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void forcePower(Worker worker, Space targetSpace) {
        throw new UnsupportedOperationException("Should be a fatal error");
    }
}

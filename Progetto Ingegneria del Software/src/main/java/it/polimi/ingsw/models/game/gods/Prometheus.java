package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.GodPower;

/**
 * Not implemented yet
 */
public class Prometheus extends God {


    @Override
    public void activateGodPower(Worker worker) {
            worker.getRules().addBuildRules("prometheusPower", GodPower::prometheusPower);
    }

    @Override
    public void deactivateGodPower(Worker worker) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

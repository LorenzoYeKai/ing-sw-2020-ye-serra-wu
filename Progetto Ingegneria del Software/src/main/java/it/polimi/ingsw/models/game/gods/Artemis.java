package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.GodPower;

public class Artemis extends God{

    /**
     * Can move 2 times (not to the original space) in a single turn
     */
    @Override
    public void workerActionOrder(Worker worker){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void activateGodPower(Worker worker) {
        worker.getRules().addMovementRules("artemisPower", GodPower::artemisPower);
    }

    @Override
    public void deactivateGodPower(Worker worker) {
        worker.getRules().getMovementRules().remove("artemisPower");
    }

    @Override
    public void forcePower(Worker worker, Space targetSpace) {
        throw new UnsupportedOperationException("Should be a fatal error");
    }

}

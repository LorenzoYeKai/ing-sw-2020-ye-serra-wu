package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.DefaultRule;
import it.polimi.ingsw.models.game.rules.GodPower;

/**
 * Not implemented yet
 */
public class Minotaur extends God {

    //Default action order

    @Override
    public void activateGodPower(Worker worker) {
        worker.getRules().addMovementRules("minotaurPower", GodPower::minotaurPower);
        worker.getRules().getMovementRules().remove("defaultIsFreeFromWorker");
    }

    @Override
    public void deactivateGodPower(Worker worker) {
        worker.getRules().getMovementRules().remove("minotaurPower");
        worker.getRules().addMovementRules("defaultIsFreeFromWorker", DefaultRule::defaultIsFreeFromWorker);
    }

    @Override
    public void forcePower(Worker worker, Space targetSpace){
        if(targetSpace.getWorkerData().getPlayer().equals(worker.getPlayer())){
            throw new UnsupportedOperationException("Should be fatal error");
        }
        Worker opponentWorker = targetSpace.getWorker();
        worker.push(opponentWorker, targetSpace);
    }
}

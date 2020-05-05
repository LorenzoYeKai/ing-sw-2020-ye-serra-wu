package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.DefaultRule;
import it.polimi.ingsw.models.game.rules.GodPower;

/**
 * Not implemented yet
 */
public class Apollo extends God {

    @Override
    public void forcePower(Worker worker, Space targetSpace){
        if(targetSpace.getWorkerData().getPlayer().equals(worker.getPlayer())){
            throw new UnsupportedOperationException("Should be fatal error");
        }
        Worker opponentWorker = targetSpace.getWorker();
        targetSpace.removeWorker();
        worker.move(targetSpace);
        worker.getInitialSpace().setWorker(opponentWorker);
    }

    @Override
    public void activateGodPower(Worker worker) {
        worker.getRules().addMovementRules("forcingPower", GodPower::forcingPower);
        worker.getRules().getMovementRules().remove("defaultIsFreeFromWorker");
    }

    @Override
    public void deactivateGodPower(Worker worker) {
        worker.getRules().getMovementRules().remove("forcingPower");
        worker.getRules().addMovementRules("defaultIsFreeFromWorker", DefaultRule::defaultIsFreeFromWorker);

    }
}

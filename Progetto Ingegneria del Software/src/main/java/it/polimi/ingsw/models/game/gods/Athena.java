package it.polimi.ingsw.models.game.gods;


import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.GodPower;

import java.util.ArrayList;
import java.util.List;


public class Athena extends God {

    /**
     * At the beginning of the turn deactivates the power
     * If the worker moves up activates the power
     */
    @Override
    public List<WorkerActionType> workerActionOrder(int phase, Worker worker){
        List<WorkerActionType> actionOrder = new ArrayList<>();
        if(phase == 0){
            this.deactivateGodPower(worker);
            actionOrder.add(WorkerActionType.MOVE);
        }
        if(phase == 1){
            if(worker.getCurrentSpace().levelDifference(worker.previousSpace()) > 0){ //if the worker moves up
                this.activateGodPower(worker);
            }
            actionOrder.add(WorkerActionType.BUILD);
            actionOrder.add(WorkerActionType.BUILD_DOME);
        }
        if(phase == 2){
            actionOrder.add(WorkerActionType.END_TURN);
        }
        return actionOrder;
    }

    /*private void activatePassivePower(Worker worker){
        worker.getRules().setRuleSets(worker.getPlayer().getRuleIndex(), new AthenaRule(worker.getWorld()));
    }*/


    @Override
    public void activateGodPower(Worker worker) {
        worker.getRules().addMovementRules("athenaPower", GodPower::cannotMoveUpPower);
    }

    @Override
    public void deactivateGodPower(Worker worker){
        worker.getRules().getMovementRules().remove("athenaPower");
    }

    @Override
    public void forcePower(Worker worker, Space targetSpace) {
        throw new UnsupportedOperationException("Should be a fatal error");
    }

}

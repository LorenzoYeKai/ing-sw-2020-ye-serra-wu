package it.polimi.ingsw.models.game.gods;


import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.ActualRule;
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
        ActualRule rules = worker.getRules();
        List<WorkerActionType> actionOrder = new ArrayList<>();
        if(phase == 0){
            this.deactivateGodPower(rules);
            actionOrder.add(WorkerActionType.MOVE);
        }
        if(phase == 1){
            //noinspection OptionalGetWithoutIsPresent
            Space previousSpace = worker.getPreviousSpace().get();
            if(worker.getCurrentSpace().levelDifference(previousSpace) > 0){ //if the worker moves up
                this.activateGodPower(rules);
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
    public void activateGodPower(ActualRule rules) {
        rules.addMovementRules("athenaPower", GodPower::cannotMoveUpPower);
    }

    @Override
    public void deactivateGodPower(ActualRule rules){
        rules.getMovementRules().remove("athenaPower");
    }

}

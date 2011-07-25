package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.models.game.rules.GodPower;

import java.util.ArrayList;
import java.util.List;

/**
 * Not implemented yet
 */
public class Prometheus extends God {


    @Override
    public List<WorkerActionType> workerActionOrder(int phase, Worker worker){
        ActualRule rules = worker.getRules();
        List<WorkerActionType> actionOrder = new ArrayList<>();
        if(phase == 0){
            this.deactivateGodPower(rules);
            actionOrder.add(WorkerActionType.MOVE);
            actionOrder.add(WorkerActionType.BUILD);
            actionOrder.add(WorkerActionType.BUILD_DOME);
        }
        if(phase == 1){
            if(!worker.isLastActionMove()){
                this.activateGodPower(rules);
                actionOrder.add(WorkerActionType.MOVE);
            }
            else {
                actionOrder.add(WorkerActionType.BUILD);
                actionOrder.add(WorkerActionType.BUILD_DOME);
            }
        }
        if(phase == 2){
            if(worker.isLastActionMove()){
                this.deactivateGodPower(rules);
                actionOrder.add(WorkerActionType.BUILD);
                actionOrder.add(WorkerActionType.BUILD_DOME);
            }
            else {
                this.deactivateGodPower(rules);
                actionOrder.add(WorkerActionType.END_TURN);
            }
        }
        if(phase == 3){
            this.deactivateGodPower(rules);
            actionOrder.add(WorkerActionType.END_TURN);
        }
        return actionOrder;
    }

    @Override
    public void activateGodPower(ActualRule rules) {
        rules.addBuildRules("prometheusPower", GodPower::cannotMoveUpPower);
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.getMovementRules().remove("prometheusPower");
    }

}

package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.GodPower;

import java.util.ArrayList;
import java.util.List;

/**
 * Not implemented yet
 */
public class Prometheus extends God {


    @Override
    public List<WorkerActionType> workerActionOrder(int phase, Worker worker){
        List<WorkerActionType> actionOrder = new ArrayList<>();
        if(phase == 0){
            this.deactivateGodPower(worker);
            actionOrder.add(WorkerActionType.MOVE);
            actionOrder.add(WorkerActionType.BUILD);
            actionOrder.add(WorkerActionType.BUILD_DOME);
        }
        if(phase == 1){
            if(!worker.hasMoved()){
                this.activateGodPower(worker);
                actionOrder.add(WorkerActionType.MOVE);
            }
            else {
                actionOrder.add(WorkerActionType.BUILD);
                actionOrder.add(WorkerActionType.BUILD_DOME);
            }
        }
        if(phase == 2){
            if(worker.hasMoved()){
                this.deactivateGodPower(worker);
                actionOrder.add(WorkerActionType.BUILD);
                actionOrder.add(WorkerActionType.BUILD_DOME);
            }
            else {
                this.deactivateGodPower(worker);
                actionOrder.add(WorkerActionType.END_TURN);
            }
        }
        if(phase == 3){
            this.deactivateGodPower(worker);
            actionOrder.add(WorkerActionType.END_TURN);
        }
        return actionOrder;
    }

    @Override
    public void activateGodPower(Worker worker) {
            worker.getRules().addBuildRules("prometheusPower", GodPower::cannotMoveUpPower);
    }

    @Override
    public void deactivateGodPower(Worker worker) {
        worker.getRules().getMovementRules().remove("prometheusPower");
    }

    @Override
    public void forcePower(Worker worker, Space targetSpace) {
        throw new UnsupportedOperationException("Should be a fatal error");
    }
}

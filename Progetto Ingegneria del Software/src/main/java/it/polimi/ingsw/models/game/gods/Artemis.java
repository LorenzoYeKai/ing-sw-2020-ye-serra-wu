package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.GodPower;

import java.util.ArrayList;
import java.util.List;

public class Artemis extends God{

    /**
     * Can move 2 times (not to the original space) in a single turn
     */
    @Override
    public List<WorkerActionType> workerActionOrder(int phase, Worker worker){
        List<WorkerActionType> actionOrder = new ArrayList<>();
        if(phase == 0){
            actionOrder.add(WorkerActionType.MOVE);
        }
        if(phase == 1){
            this.activateGodPower(worker);
            actionOrder.add(WorkerActionType.MOVE);
            actionOrder.add(WorkerActionType.BUILD);
            actionOrder.add(WorkerActionType.BUILD_DOME);
        }
        if(phase == 2){
            this.deactivateGodPower(worker);
            if(worker.hasMoved()){
                actionOrder.add(WorkerActionType.BUILD);
                actionOrder.add(WorkerActionType.BUILD_DOME);
            }
            else {
                actionOrder.add(WorkerActionType.END_TURN);
            }
        }
        if(phase == 3){
            actionOrder.add(WorkerActionType.END_TURN);
        }
        return actionOrder;
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

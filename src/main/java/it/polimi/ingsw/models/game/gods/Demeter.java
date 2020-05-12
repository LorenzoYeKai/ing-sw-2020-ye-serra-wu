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
public class Demeter extends God {

    @Override
    public List<WorkerActionType> workerActionOrder(int phase, Worker worker){
        List<WorkerActionType> actionOrder = new ArrayList<>();
        if(phase == 0){
            this.activateGodPower(worker);
            actionOrder.add(WorkerActionType.MOVE);
        }
        if(phase == 1){
            actionOrder.add(WorkerActionType.BUILD);
            actionOrder.add(WorkerActionType.BUILD_DOME);
        }
        if(phase == 2){
            if(worker.computeBuildableSpaces().size() > 0){
                actionOrder.add(WorkerActionType.BUILD);
                actionOrder.add(WorkerActionType.BUILD_DOME);

            }
            actionOrder.add(WorkerActionType.END_TURN);
        }
        if(phase == 3){
            this.deactivateGodPower(worker);
            actionOrder.add(WorkerActionType.END_TURN);
        }
        return actionOrder;
    }

    @Override
    public void activateGodPower(Worker worker) {
        worker.getRules().addBuildRules("demeterPower", GodPower::demeterPower);
        worker.getRules().getBuildRules().remove("hephaestusPower");
    }

    @Override
    public void deactivateGodPower(Worker worker) {
        worker.getRules().getBuildRules().remove("demeterPower");
    }

    @Override
    public void forcePower(Worker worker, Space targetSpace) {
        throw new UnsupportedOperationException("Should be a fatal error");
    }
}

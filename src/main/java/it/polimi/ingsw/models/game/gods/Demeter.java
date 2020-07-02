package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.ActualRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Not implemented yet
 */
public class Demeter extends God {

    @Override
    public List<WorkerActionType> workerActionOrder(int phase, Worker worker){
        ActualRule rules = worker.getRules();
        List<WorkerActionType> actionOrder = new ArrayList<>();
        if(phase == 0){
            this.activateGodPower(rules);
            actionOrder.add(WorkerActionType.MOVE);
        }
        if(phase == 1){
            actionOrder.add(WorkerActionType.BUILD);
            actionOrder.add(WorkerActionType.BUILD_DOME);
        }
        if(phase == 2){
            this.activateGodPower(rules); //for undo
            if(worker.computeBuildableSpaces().size() > 0){
                actionOrder.add(WorkerActionType.BUILD);
                actionOrder.add(WorkerActionType.BUILD_DOME);

            }
            actionOrder.add(WorkerActionType.END_TURN);
        }
        if(phase == 3){
            this.deactivateGodPower(rules);
            actionOrder.add(WorkerActionType.END_TURN);
        }
        return actionOrder;
    }

    @Override
    public void activateGodPower(ActualRule rules) {
        rules.addBuildRules("demeterPower", (worker, target) ->
                worker.getPreviousBuild().map(space ->
                        !space.getPosition().equals(target.getPosition())
                ).orElse(true)
        );
        // TODO: WHAT?
        rules.getBuildRules().remove("hephaestusPower");
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.getBuildRules().remove("demeterPower");
    }

}

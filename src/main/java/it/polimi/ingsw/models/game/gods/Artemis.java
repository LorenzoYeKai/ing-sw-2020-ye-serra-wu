package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.ActualRule;

import java.util.ArrayList;
import java.util.List;

public class Artemis extends God {

    /**
     * Can move 2 times (not to the original space) in a single turn
     */
    @Override
    public List<WorkerActionType> workerActionOrder(int phase, Worker worker) {
        ActualRule rules = worker.getRules();
        List<WorkerActionType> actionOrder = new ArrayList<>();
        if (phase == 0) {
            this.deactivateGodPower(rules); //for undo
            actionOrder.add(WorkerActionType.MOVE);
        }
        if (phase == 1) {
            this.activateGodPower(rules);
            actionOrder.add(WorkerActionType.MOVE);
            actionOrder.add(WorkerActionType.BUILD);
            actionOrder.add(WorkerActionType.BUILD_DOME);
        }
        if (phase == 2) {
            this.deactivateGodPower(rules);
            if (worker.isLastActionMove()) {
                actionOrder.add(WorkerActionType.BUILD);
                actionOrder.add(WorkerActionType.BUILD_DOME);
            } else {
                actionOrder.add(WorkerActionType.END_TURN);
            }
        }
        if (phase == 3) {
            actionOrder.add(WorkerActionType.END_TURN);
        }
        return actionOrder;
    }

    @Override
    public void activateGodPower(ActualRule rules) {
        // Used only for the second movement of Artemis which cannot move back to the initial position
        rules.addMovementRules("artemisPower", (worker, target) -> {
            //noinspection OptionalGetWithoutIsPresent
            Space previous = worker.getPreviousSpace().get();
            return !target.getPosition().equals(previous.getPosition());
        });
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.getMovementRules().remove("artemisPower");
    }

}

package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Vector2;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.ActualRule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Abstract class that handles the action orders depending on the God every player has chosen.
 *
 */
public abstract class God implements Serializable {

    public void onTurnStarted(ActualRule rules) {
        this.activateGodPower(rules);
    }

    public void onTurnEnded(Worker workerUsed, ActualRule rules) {
        this.deactivateGodPower(rules);
    }

    /**
     *
     * @param phase the phase of the turn
     * @param worker the worker witch wants to move
     * @return all possible action that the worker can perform
     */
    public final List<WorkerActionType> workerActionOrder(int phase, Worker worker) {
        List<WorkerActionType> possibleActionsList = new ArrayList<>();
        Map<WorkerActionType, List<Vector2>> possibleActions = worker.computePossibleActions();
        for(WorkerActionType type : possibleActions.keySet()) {
            // if there are any possible actions (list of Vector2 is not empty)
            if(!possibleActions.get(type).isEmpty()) {
                // then add it to the list
                possibleActionsList.add(type);
            }
        }
        return possibleActionsList;
    }

    /**
     * Activates the god power in Actual Rules
     * @param rules
     */
    abstract public void activateGodPower(ActualRule rules);

    abstract public void deactivateGodPower(ActualRule rules);

    /**
     * Trigger the "force power", i.e. the ability to move to an occupied space
     * And "force" the opponent worker to move away.
     *
     * @param worker the worker which wants to move
     * @param target the destination space
     */
    public void forcePower(Worker worker, Space target) {
        throw new InternalError("This god does not have force power");
    }


    /*public void deactivatePassivePower(Worker worker){
        worker.getRules().setRuleSets(worker.getPlayer().getRuleIndex(), new DefaultRule(worker.getWorld()));
    }*/


}

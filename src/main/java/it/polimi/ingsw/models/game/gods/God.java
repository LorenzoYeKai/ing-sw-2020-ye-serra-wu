package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.ActualRule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the default turn of a worker
 */
public abstract class God implements Serializable {


    public List<WorkerActionType> workerActionOrder(int phase, Worker worker){ //potrei usarlo solo e soltanto per ordinare le operazioni e aggiungerle a seconda del god
        ActualRule rules = worker.getRules();
        List<WorkerActionType> actionOrder = new ArrayList<>();
        if(phase == 0){
            this.activateGodPower(rules);
            actionOrder.add(WorkerActionType.MOVE);
        }
        if(phase == 1){
            this.activateGodPower(rules); //for undo
            actionOrder.add(WorkerActionType.BUILD);
            actionOrder.add(WorkerActionType.BUILD_DOME);
        }
        if(phase == 2){
            this.deactivateGodPower(rules);
            actionOrder.add(WorkerActionType.END_TURN);
        }
        return actionOrder;
    }


    abstract public void activateGodPower(ActualRule rules);

    abstract public void deactivateGodPower(ActualRule rules);

    /**
     * Trigger the "force power", i.e. the ability to move to an occupied space
     * And "force" the opponent worker to move away.
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

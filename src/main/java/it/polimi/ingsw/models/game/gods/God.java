package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the default turn of a worker
 */
public abstract class God implements Serializable {


    public List<WorkerActionType> workerActionOrder(int phase, Worker worker){ //potrei usarlo solo e soltanto per ordinare le operazioni e aggiungerle a seconda del god
        List<WorkerActionType> actionOrder = new ArrayList<>();
        if(phase == 0){
            this.activateGodPower(worker);
            actionOrder.add(WorkerActionType.MOVE);
        }
        if(phase == 1){
            this.activateGodPower(worker); //for undo
            actionOrder.add(WorkerActionType.BUILD);
            actionOrder.add(WorkerActionType.BUILD_DOME);
        }
        if(phase == 2){
            this.deactivateGodPower(worker);
            actionOrder.add(WorkerActionType.END_TURN);
        }
        return actionOrder;
    }


    abstract public void activateGodPower(Worker worker);

    abstract public void deactivateGodPower(Worker worker);

    abstract public void forcePower(Worker worker, Space targetSpace);

    /*public void deactivatePassivePower(Worker worker){
        worker.getRules().setRuleSets(worker.getPlayer().getRuleIndex(), new DefaultRule(worker.getWorld()));
    }*/





}

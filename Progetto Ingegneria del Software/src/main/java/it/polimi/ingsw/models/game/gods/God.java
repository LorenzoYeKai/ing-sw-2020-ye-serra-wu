package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.Worker;

import java.util.Scanner;
import java.util.function.BiConsumer;

/**
 * Manages the default turn of a worker
 */
public abstract class God {

    /**
     * Default order of action performed by a worker in one turn
     */
    public void workerActionOrder(Worker worker){ //potrei usarlo solo e soltanto per ordinare le operazioni e aggiungerle a seconda del god
        throw new UnsupportedOperationException("Not implemented yet");
    }

    abstract public void activateGodPower(Worker worker);

    abstract public void deactivateGodPower(Worker worker);

    /*public void deactivatePassivePower(Worker worker){
        worker.getRules().setRuleSets(worker.getPlayer().getRuleIndex(), new DefaultRule(worker.getWorld()));
    }*/





}
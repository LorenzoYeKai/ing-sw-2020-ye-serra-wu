package it.polimi.ingsw.server;

import it.polimi.ingsw.models.game.Worker;

import java.io.Serializable;
import java.util.List;

public class AvailableWorkersDisplay extends ServerMessage implements Serializable {

    private List<Integer> availableWorkers;

    public AvailableWorkersDisplay(List<Integer> availableWorkers){
        this.availableWorkers = availableWorkers;
    }

    @Override
    public void displayMessage() {
        System.out.println("\nHere are the available workers: ");
        this.availableWorkers.forEach(i -> System.out.println("Worker " + i));
        System.out.println("Select one worker, type \"select number\": ");
    }
}

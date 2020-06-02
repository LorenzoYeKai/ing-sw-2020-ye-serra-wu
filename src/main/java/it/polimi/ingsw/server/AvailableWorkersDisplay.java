package it.polimi.ingsw.server;

import it.polimi.ingsw.models.game.Worker;

import java.io.Serializable;
import java.util.List;

public class AvailableWorkersDisplay extends ServerMessage implements Serializable {


    @Override
    public void displayMessage() {
        System.out.println("\nHere are the available workers: ");
        System.out.println("Worker 0");
        System.out.println("Worker 1");
        System.out.println("Select one worker, type \"select number\": ");
    }
}

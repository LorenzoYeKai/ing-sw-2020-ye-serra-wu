package it.polimi.ingsw.client;

import it.polimi.ingsw.models.game.Worker;

import java.io.Serializable;

public class AvailableActionsReply extends ClientReply implements Serializable {

    private String name;
    private Worker worker;

    public AvailableActionsReply(String clientName) {
        super(clientName);
    }

    public String getName(){return this.name;}

    public Worker getWorker(){ return this.worker;}
}

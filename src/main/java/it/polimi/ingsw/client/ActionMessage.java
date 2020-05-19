package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.game.WorkerActionType;

import java.io.Serializable;

public class ActionMessage extends ClientReply implements Serializable {

    private WorkerActionType actionType;
    private int x;
    private int y;

    public ActionMessage(String clientName) {
        super(clientName);
    }


    public WorkerActionType getType() {
        return this.actionType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}


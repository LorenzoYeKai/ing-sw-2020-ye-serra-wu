package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.game.WorkerActionType;

public class ChooseActionTurnMessage {
    private WorkerActionType actionType;
    private int x;
    private int y;


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


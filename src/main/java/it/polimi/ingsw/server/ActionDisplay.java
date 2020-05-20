package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.gods.GodType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionDisplay extends ServerMessage {


    private List<WorkerActionType> actions;


    public ActionDisplay(List<WorkerActionType> actions){
        this.actions = actions;
    }


    @Override
    public void displayMessage() {
        System.out.println("Here is what you can do:");
        for (WorkerActionType action : this.actions) {
            System.out.print(action + ": ");
            switch (action) {
                case MOVE:
                    System.out.println("move x,y");
                case BUILD:
                    System.out.println("build x,y");
                case BUILD_DOME:
                    System.out.println("dome x,y");
                case END_TURN:
                    System.out.println("end");
            }
        }
    }
}

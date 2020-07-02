package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Vector2;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ActionDisplay extends ServerMessage implements Serializable {


    private Map<WorkerActionType, List<Vector2>> actions;


    public ActionDisplay(Map<WorkerActionType, List<Vector2>> actions){
        this.actions = actions;
    }


    @Override
    public void displayMessage() {
        System.out.println("Here is what you can do:");
        for (WorkerActionType action : this.actions.keySet()) {
            if(action == WorkerActionType.MOVE) {
                System.out.println(action + ": move x,y");
                System.out.println("Available spaces:");
                if(this.actions.get(action).isEmpty()){
                    System.out.println("There's no available spaces...");
                }
                else{
                    this.actions.get(action).forEach(vector2 -> System.out.println("x: [" + vector2.getX() + "] y: [" + vector2.getY() + "]"));
                }
            }
            else if(action == WorkerActionType.BUILD) {
                System.out.println(action + ": build x,y");
                System.out.println("Available spaces:");
                if(this.actions.get(action).isEmpty()){
                    System.out.println("There's no available spaces...");
                }
                else{
                    this.actions.get(action).forEach(vector2 -> System.out.println("x: [" + vector2.getX() + "] y: [" + vector2.getY() + "]"));
                }
            }
            else if(action == WorkerActionType.BUILD_DOME) {
                System.out.println(action + ": dome x,y");
                System.out.println("Available spaces:");
                if(this.actions.get(action).isEmpty()){
                    System.out.println("There's no available spaces...");
                }
                else{
                    this.actions.get(action).forEach(vector2 -> System.out.println("x: [" + vector2.getX() + "] y: [" + vector2.getY() + "]"));
                }
            }
            else if(action == WorkerActionType.END_TURN){
                System.out.println(action + ": end");
            }
        }
        System.out.println("Type \"undo\" for undo your last action!");
    }
}

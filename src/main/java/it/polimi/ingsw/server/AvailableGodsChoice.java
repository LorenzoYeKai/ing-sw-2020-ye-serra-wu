package it.polimi.ingsw.server;

import it.polimi.ingsw.models.game.gods.GodType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class AvailableGodsChoice extends ServerMessage implements Serializable {

    private Set<GodType> availableGods;

    public AvailableGodsChoice(Set<GodType> availableGods){
        this.availableGods = availableGods;
    }

    @Override
    public void displayMessage(){
        System.out.println("AvailableGods:");
        this.availableGods.forEach(System.out::println);
    }


}

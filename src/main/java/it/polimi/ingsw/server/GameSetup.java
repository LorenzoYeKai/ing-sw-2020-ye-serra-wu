package it.polimi.ingsw.server;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameSetup extends ServerMessage implements Serializable {

    private static final long serialVersionUID = 5L;

    private List<GodType> allAvailableGods;
    private String worldInfo;
    private List<String> listOfPlayers;

    public GameSetup(List<GodType> allAvailableGods, World emptyWorld, List<Player> listOfPlayers){
        this.allAvailableGods= allAvailableGods;
        //this.worldInfo = emptyWorld.printWorld(listOfPlayers);
        this.listOfPlayers = new ArrayList<>();
        listOfPlayers.forEach(p -> this.listOfPlayers.add(p.getName()));

    }

    public List<GodType> getAllAvailableGods() {
        return allAvailableGods;
    }

    public List<String> getListOfPlayers() {
        return listOfPlayers;
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
    }

    @Override
    public void displayMessage() {
        System.out.println("List of Players:");
        listOfPlayers.forEach(System.out::println);
        System.out.println("List of all available gods");
        allAvailableGods.forEach(System.out::println);
        System.out.println("World:");
        System.out.println(worldInfo);
    }
}

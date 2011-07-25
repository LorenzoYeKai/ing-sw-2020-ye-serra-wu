package it.polimi.ingsw.server;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.gods.GodType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldDisplay extends ServerMessage implements Serializable {


    private String worldInfo;
    private Map<String, GodType> listOfPlayersAndGods;


    public WorldDisplay(Game game){
        //this.worldInfo = game.getWorld().printWorld(game.getListOfPlayers());
        this.listOfPlayersAndGods = new HashMap<>();
        game.getListOfPlayers().forEach(p -> this.listOfPlayersAndGods.put(p.getName(), p.getGodType(p.getGod())));
    }

    @Override
    public void displayMessage() {
        System.out.println("\nCurrent active players: ");
        for(int i = 0; i < this.listOfPlayersAndGods.size(); i++){
            System.out.println(new ArrayList<>(this.listOfPlayersAndGods.keySet()).get(i) + ": " + new ArrayList<>(this.listOfPlayersAndGods.values()).get(i));
        }
        System.out.println(worldInfo);
    }
}

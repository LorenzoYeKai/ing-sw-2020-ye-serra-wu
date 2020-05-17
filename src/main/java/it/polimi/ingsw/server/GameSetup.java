package it.polimi.ingsw.server;

import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.gods.GodType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

public class GameSetup implements Serializable {

    private static final long serialVersionUID = 5L;

    private List<GodType> allAvailableGods;
    private World emptyWorld;
    private List<Player> listOfPlayers;

    public GameSetup(List<GodType> allAvailableGods,World emptyWorld,List<Player> listOfPlayers){
        this.allAvailableGods= allAvailableGods;
        this.emptyWorld = emptyWorld;
        this.listOfPlayers=listOfPlayers;
    }



    public List<GodType> getAllAvailableGods() {
        return allAvailableGods;
    }

    public World getEmptyWorld() {
        return emptyWorld;
    }

    public List<Player> getListOfPlayers() {
        return listOfPlayers;
    }

    public void displaySetupMessage(){
        // differenziazzione stampa del display in base al challenger e agli altri giocatori
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
    }
}

package it.polimi.ingsw.server;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.World;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class WorldDisplay implements Serializable {

    private Game game;
    private World worldCopy;


    public WorldDisplay(Game game){
        this.game= game;
        worldCopy= new World(game.getWorld());
    }

    public void printWorld(){
        // Vedere codice del printword vecchio se si può usare
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        printWorld();
    }
}

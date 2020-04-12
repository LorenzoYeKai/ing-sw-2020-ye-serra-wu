package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;

import java.util.ArrayList;
import java.util.Scanner;

public class Player implements PlayerData {

    private final String name;

    private Player nextPlayer;

    private boolean defeat = false;

    private final Worker[] workers = new Worker[2];

    private /*final*/ God god;

    public final Game game;

    public Player(Game game, String name) { //Nella creazione dei player saranno assegnati i rule index in modo crescente
        this.game = game;
        this.name = name;
        this.workers[0] = null;
        this.workers[1] = null;
    }

    public String getName() {
        return this.name;
    }

    /*private void setName(InputStream in, PrintStream out) { //Non servirà più visto che il nome si sceglie quando si entra nella lobby dove i player non sono ancora costruiti
        out.println("Choose a name: ");
        Scanner scanner = new Scanner(in);
        this.name = scanner.nextLine();
    }*/

    /**
     * Sets the God that will be used by this player
     */
    public void setGod(GodType type) {
        try {
            GodFactory factory = new GodFactory();
            this.god = factory.getGod(type);
        }
        catch(Exception e) { //It should never happen, if it happens the game crushes
            System.out.println("Fatal Error");
        }
    }

    /**
     * Creates and places the workers of this player on the World
     */
    public void setWorkers(int index, Space space) {
        this.workers[index] = new Worker(this);
        this.workers[index].setPosition(space);
    }

    public Worker selectWorker(int index) {
        return workers[index];
    }


    public God getGod(){
        return this.god;
    }

    /**
     * First method to be called at the beginning of the turn (Unless there is a god power that has to be activated before)
     * Computes workers' available spaces and returns the availableWorkers
     */
    public ArrayList<Worker> getAvailableWorkers(){
        ArrayList<Worker> availableWorkers = new ArrayList<Worker>();
        for(Worker w : this.workers){
            w.computeAvailableSpaces();
            if(w.getAvailableSpaces().size() != 0){
                availableWorkers.add(w);
            }
        }
        return availableWorkers;
    }
}



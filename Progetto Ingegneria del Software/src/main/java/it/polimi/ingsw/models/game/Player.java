package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Player implements PlayerData {

    private final String name;
    private final Game game;
    private final Worker[] workers;
    private God god;
    private boolean defeated;

    public Player(Game game, String name) { //Nella creazione dei player saranno assegnati i rule index in modo crescente
        this.game = game;
        this.name = name;
        this.workers = new Worker[] { new Worker(this), new Worker(this) };
        this.defeated = false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isDefeated() {
        return this.defeated;
    }

    @Override
    public List<Worker> getAllWorkers() {
        return List.of(this.workers);
    }

    @Override
    public List<Worker> getAvailableWorkers() {
        List<Worker> availableWorkers = new ArrayList<>();
        for (Worker w : this.workers) {
            if (this.game.getRules().getAvailableSpaces(w.getWorld().getSpaces(w.getX(), w.getY())).size() != 0) {
                availableWorkers.add(w);
            }
        }
        return Collections.unmodifiableList(availableWorkers);
    }

    public Game getGame() {
        return this.game;
    }

    public God getGod() {
        return this.god;
    }

    /**
     * Sets the God that will be used by this player
     */
    public void setGod(GodType type) {
        try {
            GodFactory factory = new GodFactory();
            this.god = factory.getGod(type);
        } catch (Exception e) { //It should never happen, if it happens the game crushes
            System.out.println("Fatal Error");
        }
    }

    public Worker getWorker(int index) {
        return workers[index];
    }

    public void setDefeated() {
        this.defeated = true;
    }




}



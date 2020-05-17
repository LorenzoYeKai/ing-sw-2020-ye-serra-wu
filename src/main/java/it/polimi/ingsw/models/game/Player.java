package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.InternalError;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.ActualRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Player implements PlayerData {

    private final String name;
    private final Game game;
    private final List<Worker> workers;
    private God god;
    private boolean defeated;

    public Player(Game game, String name) {
        this.game = game;
        this.name = name;
        this.workers = List.of(new Worker(this, 0), new Worker(this, 1));
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
        return this.workers;
    }

    @Override
    public List<Worker> getAvailableWorkers() {
        return this.workers.stream()
                .filter(worker -> worker.computeAvailableSpaces().size() > 0)
                .collect(Collectors.toUnmodifiableList());
    }

    public Game getGame() {
        return this.game;
    }

    @Override
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

    public Worker getWorker(WorkerData data) {
        int index = this.workers.indexOf(data);
        if (index == -1) {
            throw new InternalError("Invalid worker");
        }
        return this.workers.get(index);
    }

    public void setDefeated() {
        this.defeated = true;
    }

    public int getIndex() {
        for (int i = 0; i < game.getNumberOfActivePlayers(); i++) {
            if (this.name.equals(game.getListOfPlayers().get(i).getName())) {
                return i;
            }
        }
        throw new IllegalArgumentException("Player not found");
    }


}



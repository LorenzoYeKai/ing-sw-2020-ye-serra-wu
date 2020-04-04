package it.polimi.ingsw;

import java.lang.UnsupportedOperationException;
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.HashSet;

public class Game {

    private final GodFactory factory;
    private final ArrayList<Player> listOfPlayers;
    private HashSet<GodType> availableGods;
    private final World world;
    private ActualRule rules;

    private int currentTurn;

    /**
     * Creare una nuova partita di gioco
     *
     * @param numberOfPlayers il numero di giocatori in questa partita
     */
    public Game(int numberOfPlayers) {
        this.factory = new GodFactory();
        this.listOfPlayers = new ArrayList<Player>();
        for (int i = 0; i < numberOfPlayers; ++i) {
            this.listOfPlayers.add(new Player(this, i));
        }
        this.availableGods = null;
        this.currentTurn = -1;
        this.world = new World();
        rules = new ActualRule(this.getNumberOfPlayers(), this.world);
    }

    /**
     * Questo metodo dovrebbe essere usato dal Challenger
     * per definire le divinita' che possono essere usati.
     *
     * @param availableGodTypes I god che potrebbero essere usati, scelti dal Challenger.
     */
    public void setAvailableGods(GodType[] availableGodTypes) {
        if (this.availableGods != null) {
            throw new UnsupportedOperationException("Available gods already set");
        }
        if (this.listOfPlayers.size() != availableGodTypes.length) {
            throw new IllegalArgumentException("Number of gods is different from number of players");
        }
        this.availableGods = new HashSet<GodType>(availableGods);
        if (this.availableGods.size() != availableGodTypes.length) {
            throw new IllegalArgumentException("Challenger may have selected duplicated god types");
        }
    }

    /**
     * Verifica se e' possibile scegliere una divinita' in questa partita.
     *
     * @param type La divinita' che si vuole scegliere
     * @return {@literal true} se questa divinita' puo' essere scelta, altrimenti {@literal false}
     */
    public boolean isGodAvailable(GodType type) {
        return this.availableGods.contains(type);
    }

    /**
     * Scegliere una divinita' e ottenere i lavoratori di questa divinita'
     *
     * @param type   la divinita' che si vuole scegliere
     * @param player il giocatore
     * @return i lavoratori di questa divinita'
     */
    public God chooseGod(GodType type, Player player) {
        if (!this.isGodAvailable(type)) {
            throw new UnsupportedOperationException("God" + type + " is not available");
        }
        this.availableGods.remove(type);
        return factory.getGod(type, rules);
    }

    /**
     * Questo metodo dovrebbe essere usato dal challenger per definire il primo giocatore
     *
     * @param index Indice del primo giocatore
     */
    public void setFirstPlayer(int index) {
        if (this.currentTurn != -1) {
            throw new UnsupportedOperationException("First player was already set");
        }
        this.currentTurn = index;
    }

    /**
     * Iniziare la partita
     */
    public void startGame() {
        if (this.currentTurn == -1) {
            throw new UnsupportedOperationException("First player not chosen yet");
        }
        if (this.availableGods == null) {
            throw new UnsupportedOperationException("Available gods not set yet");
        }
        if (this.availableGods.size() != 0) {
            throw new UnsupportedOperationException("Some players may have not chosen god yet");
        }

        // Probabilmente implementare fare qualcos'altro nel futuro
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Finire la partita
     */
    public void endGame() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Ottenere il giocatore di questo turno
     * @return Il giocatore di questo turno
     */
    public Player getCurrentPlayer() {
        return this.listOfPlayers.get(this.currentTurn);
    }

    public void setCurrentPlayer() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public int getNumberOfPlayers() {
        return this.listOfPlayers.size();
    }

    public void getCurrentTurn() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Spostarsi nel prossimo turno
     */
    public void goToNextTurn() {
        // ci potrebbe essere delle verifiche da fare prima di andare nel prossimo turno...

        this.currentTurn = ((this.currentTurn + 1) % this.listOfPlayers.size());
    }

    public World getWorld(){
        return this.world;
    }

    public ActualRule getRules(){
        return this.rules;
    }
}

package it.polimi.ingsw.models.game;

import it.polimi.ingsw.Notifier;
import it.polimi.ingsw.models.InternalError;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.ActualRule;

import it.polimi.ingsw.views.game.GameView;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Game {
    private final Notifier<GameStatus> gameStatusNotifier;
    private final Notifier<SpaceData> spaceChangedNotifier;
    private final Notifier<PlayerData> turnChangedNotifier;
    private final Notifier<PlayerData> playerLostNotifier;


    private final GodFactory factory;
    private Set<GodType> availableGods;
    private final World world;
    private World previousWorld;
    private ActualRule rules;
    private final List<Player> listOfPlayers;
    private final Map<String, GameView> gameViews;

    private GameStatus currentStatus;
    private int currentTurn;

    /**
     * Creates a new game
     *
     * @param nicknames the nicknames of the players
     */
    public Game(List<String> nicknames) {
        this.gameStatusNotifier = new Notifier<>();
        this.spaceChangedNotifier = new Notifier<>();
        this.turnChangedNotifier = new Notifier<>();
        this.playerLostNotifier = new Notifier<>();

        this.factory = new GodFactory();
        this.availableGods = null;
        this.world = new World(this.spaceChangedNotifier);
        this.previousWorld = null;
        this.rules = new ActualRule(this.world);

        this.listOfPlayers = IntStream.range(0, nicknames.size())
                .mapToObj(i -> i == 0
                        ? new Challenger(this, nicknames.get(i))
                        : new Player(this, nicknames.get(i)))
                .collect(Collectors.toUnmodifiableList());
        this.gameViews = new HashMap<>();

        this.currentTurn = -1;
        this.currentStatus = GameStatus.PLAYER_JOINING;
    }

    public void attachView(String name, GameView view) {
        if(this.gameViews.containsKey(name)) {
            throw new InternalError("Player already exist");
        }

        this.gameStatusNotifier.addListener(view, view::notifyGameStatus);
        this.spaceChangedNotifier.addListener(view, view::notifySpaceChange);
        this.turnChangedNotifier.addListener(view, view::notifyPlayerTurn);
        this.playerLostNotifier.addListener(view, view::notifyPlayerDefeat);
    }

    public void detachView(String name, GameView view) {
        if(!this.gameViews.containsKey(name)) {
            throw new InternalError("Invalid player name");
        }

        this.gameStatusNotifier.removeListener(view);
        this.spaceChangedNotifier.removeListener(view);
        this.turnChangedNotifier.removeListener(view);
        this.playerLostNotifier.removeListener(view);

        this.gameViews.remove(name);

        // If there isn't any view related to a player
        // It means this player has been disconnected
        Player player = this.findPlayerByName(name);
        if(player != null) {
            // TODO: implement disconnect
            throw new InternalError("Not implemented yet");
        }
    }

    /**
     * Challenger should call this method to set available gods.
     *
     * @param availableGodTypes The gods which can be used.
     */
    public void setAvailableGods(GodType[] availableGodTypes) {
        if (this.availableGods != null) {
            throw new InternalError("Available gods already set");
        }
        if (this.listOfPlayers.size() != availableGodTypes.length) {
            throw new InternalError("Number of gods is different from number of players");
        }
        this.availableGods = new HashSet<>(Arrays.asList(availableGodTypes));
        if (this.availableGods.size() != availableGodTypes.length) {
            throw new InternalError("Challenger may have selected duplicated god types");
        }
    }

    public boolean isGodAvailable(GodType type) {
        return this.availableGods.contains(type);
    }

    public God chooseGod(GodType type, Player player) {
        if (availableGods == null) {
            throw new InternalError("The Challenger has not chosen the Gods available for this game yet");
        }
        if (!listOfPlayers.contains(player)) {
            throw new InternalError("Intruder!"); //Per sicurezza, anche se non dovrebbe capitare
        }
        if (!this.isGodAvailable(type)) {
            throw new InternalError("God" + type + " is not available");
        }
        this.availableGods.remove(type);
        return factory.getGod(type);
    }

    /**
     * Setup the game. Set worker positions, let challenger choose gods etc.
     */
    public void setupGame() {
        this.setCurrentStatus(GameStatus.SETUP);

        // TODO: Set start player / gods etc.
        this.availableGods = Collections.emptySet();

        for (int i = 0; i < this.listOfPlayers.size(); ++i) {
            // let player place worker
            this.goToNextTurn();
            this.waitUntilTurnFinished();
        }
    }

    /**
     * Play the game.
     */
    public void playGame() {
        this.setCurrentStatus(GameStatus.PLAYING);

        if (this.availableGods == null) {
            throw new InternalError("Available gods not set yet");
        }
        if (this.availableGods.size() != 0) {
            throw new InternalError("Some players may have not chosen god yet");
        }

        // TODO: IMPLEMENT GAME VIEW DISCONNECT
        while (this.getNumberOfActivePlayers() > 1) {
            this.goToNextTurn();
            this.waitUntilTurnFinished();
        }

        this.setCurrentStatus(GameStatus.ENDED);
    }

    public void announceVictory(Player winner) {
        for (Player player : this.listOfPlayers) {
            if (player != winner) {
                this.announceDefeat(player);
            }
        }
    }

    public void announceDefeat(Player player) {
        player.setDefeated();
        this.playerLostNotifier.notify(player);
    }

    private void waitUntilTurnFinished() {
        // Currently it's empty
        // Because we don't implement threading yet.
    }

    public Player getCurrentPlayer() {
        return this.listOfPlayers.get(this.currentTurn);
    }

    public Player findPlayerByName(String name) {
        for(Player player : this.listOfPlayers) {
            if(player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public int getNumberOfActivePlayers() {
        return (int) this.listOfPlayers.stream()
                .filter(player -> !player.isDefeated())
                .count();
    }

    /**
     * Go to the next turn, and notify every attached view that turn has changed
     */
    public void goToNextTurn() {
        this.currentTurn = ((this.currentTurn + 1) % this.listOfPlayers.size());
        this.turnChangedNotifier.notify(this.getCurrentPlayer());
    }

    public World getWorld() {
        return this.world;
    }

    public ActualRule getRules() {
        return this.rules;
    }

    public int getNumberOfAvailableGods() {
        return availableGods.size();
    }

    public GameStatus getCurrentStatus() {
        return this.currentStatus;
    }

    public void setCurrentStatus(GameStatus status) {
        this.currentStatus = status;
        this.gameStatusNotifier.notify(this.currentStatus);
    }

    public void savePreviousWorld(){
        this.previousWorld = new World(this.world);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Worker w = this.world.getSpaces(i, j).getWorker();
                if(w != null){
                    this.previousWorld.getSpaces(i, j).setWorker(new Worker(w));
                }
            }
        }
    }

    public World getPreviousWorld(){
        return this.previousWorld;
    }

    /**
     * Solo per i test, da togliere!!!!
     */
    public void setCurrentTurn(int i){
        this.currentTurn = i;
    }
}

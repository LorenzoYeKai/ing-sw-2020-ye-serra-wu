package it.polimi.ingsw.models.game;

import it.polimi.ingsw.Notifier;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.ActualRule;

import it.polimi.ingsw.views.game.GameView;

import java.util.*;


/**
 *
   It is the main class of the model and allows us to reach all the information easily.
 *
 *
 *
 */
public class Game {

    private final Notifier<GameStatus> gameStatusNotifier;
    private final Notifier<Collection<GodType>> availableGodsNotifier;
    private final Notifier<Map<String, GodType>> playerGodsNotifier;
    private final Notifier<Space> spaceChangedNotifier;
    private final Notifier<String> turnChangedNotifier;
    private final Notifier<String> playerLostNotifier;

    private final GodFactory factory;
    private final Set<GodType> availableGods;
    private final World world;
    private final ActualRule rules;

    private final List<Player> listOfPlayers;
    private final Map<String, GodType> playerGods;
    private final Map<String, GameView> gameViews;

    private int currentPlayer;
    private GameStatus status;

    // this will be reset by each selectWorker() of controller
    private boolean currentWorkerHasAlreadyMoved = false;
    private Map<WorkerActionType, List<Vector2>> currentWorkerValidActions = null;

    /**
     * Creates a new game
     *
     * @param nicknames the nicknames of the players
     */
    public Game(List<String> nicknames) {
        this.gameStatusNotifier = new Notifier<>();
        this.availableGodsNotifier = new Notifier<>();
        this.playerGodsNotifier = new Notifier<>();
        this.spaceChangedNotifier = new Notifier<>();
        this.turnChangedNotifier = new Notifier<>();
        this.playerLostNotifier = new Notifier<>();

        this.factory = new GodFactory();
        this.availableGods = new HashSet<>();
        this.world = new World(this.spaceChangedNotifier);
        this.rules = new ActualRule(this.world);

        this.listOfPlayers = new ArrayList<>();
        nicknames.forEach(n -> listOfPlayers.add(new Player(this, n)));
        this.playerGods = new HashMap<>();
        this.gameViews = new HashMap<>();

        this.currentPlayer = -1;
        this.status = GameStatus.SETUP;
    }

    public void attachView(String name, GameView view) {
        if(this.listOfPlayers.stream().noneMatch(player -> player.getName().equals(name))) {
            throw new InternalError("Invalid player name");
        }

        if (this.gameViews.containsKey(name)) {
            throw new InternalError("Player already exist");
        }

        this.gameStatusNotifier.addListener(view, view::notifyGameStatus);
        this.availableGodsNotifier.addListener(view, view::notifyAvailableGods);
        this.playerGodsNotifier.addListener(view, view::notifyPlayerGods);
        this.spaceChangedNotifier.addListener(view, view::notifySpaceChange);
        this.turnChangedNotifier.addListener(view, view::notifyPlayerTurn);
        this.playerLostNotifier.addListener(view, view::notifyPlayerDefeat);

        if(this.gameViews.size() == this.listOfPlayers.size()) {
            this.setStatus(GameStatus.SETUP);
        }
    }

    public void detachView(String name, GameView view) {
        if (!this.gameViews.containsKey(name)) {
            throw new InternalError("Invalid player name");
        }

        this.gameStatusNotifier.removeListener(view);
        this.availableGodsNotifier.removeListener(view);
        this.playerGodsNotifier.removeListener(view);
        this.spaceChangedNotifier.removeListener(view);
        this.turnChangedNotifier.removeListener(view);
        this.playerLostNotifier.removeListener(view);

        this.gameViews.remove(name);

        // If there isn't any view related to a player
        // It means this player has been disconnected
        Player player = this.findPlayerByName(name);
        if (player != null) {
            // TODO: implement disconnect
            throw new InternalError("Not implemented yet");
        }
    }

    /**
     * Challenger should call this method to set available gods.
     */
    public void addAvailableGods(GodType type) {
        if (this.status != GameStatus.SETUP) {
            throw new InternalError("Cannot set available gods now");
        }
        if (this.availableGods.size() == getNumberOfActivePlayers()) {
            throw new InternalError("Available gods already set");
        }
        if (this.availableGods.contains(type)) {
            throw new InternalError("Challenger may have selected duplicated god types");
        }
        this.availableGods.add(type);
        this.availableGodsNotifier.notify(this.availableGods);

    }

    public void removeAvailableGod(GodType type) {
        if (this.status != GameStatus.SETUP) {
            throw new InternalError("Cannot set available gods now");
        }
        if (!this.availableGods.contains(type)) {
            throw new InternalError("The god is not available");
        }
        this.availableGods.remove(type);
        this.availableGodsNotifier.notify(this.availableGods);
    }

    /**
     Check that the particular god is playable for this game
     *
     * @param type god i want to check
     * @return a boolean
     */
    public boolean isGodAvailable(GodType type) {
        return this.availableGods.contains(type);
    }

    /**
     *
     It allows me to choose one of the active powers for that game
     *
     *
     * @param player player who chooses
     * @param type power I want to choose
     */
    public void chooseGod(Player player, GodType type) {
        if (this.status != GameStatus.CHOOSING_GODS) {
            throw new InternalError("Cannot choose available gods now");
        }
        if (availableGods == null) {
            throw new InternalError("The Challenger has not chosen the Gods available for this game yet");
        }
        if (!this.isGodAvailable(type)) {
            throw new InternalError("God" + type + " is not available");
        }

        player.setGod(factory.getGod(type));
        this.playerGods.put(player.getName(), type);
        this.availableGods.remove(type);
        this.availableGodsNotifier.notify(this.availableGods);
        this.playerGodsNotifier.notify(this.playerGods);
    }

    /**
     *
     It allows me to notify other players who have lost
     *
     * @param winner player who won
     */
    public void announceVictory(Player winner) {
        for (Player player : this.listOfPlayers) {
            if (player != winner) {
                this.announceDefeat(player);
            }
        }
    }

    public void announceDefeat(Player player) {
        player.setDefeated();
        this.playerLostNotifier.notify(player.getName());
    }

    public Player getCurrentPlayer() {
        return this.listOfPlayers.get(this.currentPlayer);
    }

    public Player findPlayerByName(String name) {
        for (Player player : this.listOfPlayers) {
            if (player.getName().equals(name)) {
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
        do {
            this.setCurrentPlayer((this.currentPlayer + 1) % this.listOfPlayers.size());
        }
        while (this.getCurrentPlayer().isDefeated());
    }

    // solo per i test //
    public int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    public World getWorld() {
        return this.world;
    }

    /**
     *
     allows me to take the updated rules
     *
     * @return updated rules
     */
    public ActualRule getRules() {
        return this.rules;
    }

    public int getNumberOfAvailableGods() {
        return availableGods.size();
    }

    public int getTurnPhase() {
        return this.world.getNumberOfSavedPreviousWorlds();
    }

    public void clearPreviousWorlds() {
        this.world.clearPreviousWorlds();
    }

    /**
     * Set the worker moved flag, which is helpful to
     * {@link #calculateValidWorkerActions()}
     */
    public void clearCurrentWorkerMovedFlag() {
        this.currentWorkerHasAlreadyMoved = false;
        this.currentWorkerValidActions = null;
    }

    /**
     * Clear the worker moved flag, which is helpful to
     * {@link #calculateValidWorkerActions()}
     */
    public void setCurrentWorkerMovedFlag() {
        this.currentWorkerHasAlreadyMoved = true;
        this.currentWorkerValidActions = null;
    }

    /**
     * allows me to return to the condition before the last move.
     *
     * Implemented for the undo function
     *
     * @return
     */
    public WorldData getPreviousWorld() {
        return this.world.peekPrevious().orElseThrow(() -> {
            //TODO: handle empty previousWorlds
            return new UnsupportedOperationException("Empty World error non implemented yet!");
        });
    }

    /**
     * allows me to return to the condition before the last action.
     *
     */
    public void gameUndo() {
        this.world.revertWorld();
        for (Space space : this.world.getData()) {
            if (space.isOccupiedByWorker()) {
                this.getWorker(space.getWorkerData()).reset(space);
            }
        }
    }

    public Set<GodType> getAvailableGods() {
        return this.availableGods;
    }

    /**
     *
     *setter for the choice of the first player
     *
     * @param i index to move through the list of players
     */

    public void setCurrentPlayer(int i) {

        if(this.currentPlayer != -1 && this.status == GameStatus.PLAYING) {
            // deactivate god power for previous player
            Player player = this.getCurrentPlayer();
            if(player.getGod() != null) {
                player.getGod().onTurnEnded(player.getSelectedWorker(), this.getRules());
            }
        }
        if(this.status == GameStatus.BEFORE_PLACING){
            this.setStatus(GameStatus.PLACING);
        }
        if(this.status == GameStatus.BEFORE_PLAYING){
            this.clearPreviousWorlds();
            this.clearCurrentWorkerMovedFlag();
            this.setStatus(GameStatus.PLAYING);
        }
        this.currentPlayer = i;
        this.turnChangedNotifier.notify(this.getCurrentPlayer().getName());
        // activate god power for current player
        if(this.getCurrentPlayer().getGod() != null && this.status == GameStatus.PLAYING) {
            this.getCurrentPlayer().getGod().onTurnStarted(this.getRules());
        }
    }

    public Worker getWorker(WorkerData identity) {
        return this.getListOfPlayers().stream()
                .filter(player -> player.getName().equals(identity.getPlayer()))
                .findAny()
                .map(player -> player.getWorker(identity))
                .orElseThrow(() -> new InternalError("Invalid worker data"));
    }

    public List<Player> getListOfPlayers() {
        return listOfPlayers;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
        this.gameStatusNotifier.notify(this.status);
    }

    public GameStatus getStatus() {
        return this.status;
    }

    /**
     * Get the already calculated valid worker actions.
     * If there isn't any valid worker actions for each workers,
     * then player can be defeated.
     * @return the list of valid actions
     */
    public Map<WorkerActionType, List<Vector2>> getValidWorkerActions() {
        return this.currentWorkerValidActions;
    }

    /**
     *
     *
     * Calculate all possible actions the worker selects for that turn
     *
     *
     */
    public void calculateValidWorkerActions() {
        if(this.status == GameStatus.PLACING){
            return;
        }
        this.currentWorkerValidActions = new HashMap<>();

        Worker selectedWorker = this.getCurrentPlayer().getSelectedWorker();
        var possibleActions = selectedWorker.computePossibleActions();
        for(WorkerActionType type : possibleActions.keySet()) {
            List<Vector2> possibleTargets = possibleActions.get(type);
            for (Vector2 targetPosition: possibleTargets) {
                WorkerActionPredictor predictor =
                        new WorkerActionPredictor(this, this.currentWorkerHasAlreadyMoved);
                boolean isValid = predictor.verify(selectedWorker, type, targetPosition);
                if(isValid) {
                    // create the list if necessary
                    if(!this.currentWorkerValidActions.containsKey(type)) {
                        this.currentWorkerValidActions.put(type, new ArrayList<>());
                    }
                    // this is a valid action which contains move-then-build
                    this.currentWorkerValidActions.get(type).add(targetPosition);
                }
            }
        }
    }



    private boolean checkDefeat(Map<WorkerActionType, List<Vector2>> actions) {
        for (WorkerActionType w : actions.keySet()) {
            if (w != WorkerActionType.END_TURN) {
                if (!actions.get(w).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

}

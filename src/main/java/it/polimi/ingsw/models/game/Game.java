package it.polimi.ingsw.models.game;

import it.polimi.ingsw.Notifier;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.ActualRule;

import it.polimi.ingsw.views.game.GameView;

import java.util.*;

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
    private final Map<String, GameView> gameViews;

    private int currentPlayer;
    private GameStatus status;

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
        this.availableGods = new HashSet<GodType>();
        this.world = new World(this.spaceChangedNotifier);
        this.rules = new ActualRule(this.world);

        this.listOfPlayers = new ArrayList<Player>();
        nicknames.forEach(n -> listOfPlayers.add(new Player(this, n)));
        this.gameViews = new HashMap<>();

        this.currentPlayer = -1;
        this.status = GameStatus.PLAYER_JOINING;
    }

    public void oldAttachView(String name, GameView view) {
        if (this.gameViews.containsKey(name)) {
            throw new InternalError("Player already exist");
        }

        this.gameStatusNotifier.addListener(view, view::notifyGameStatus);
        this.availableGodsNotifier.addListener(view, view::notifyAvailableGods);
        this.playerGodsNotifier.addListener(view, view::notifyPlayerGods);
        this.spaceChangedNotifier.addListener(view, view::notifySpaceChange);
        this.turnChangedNotifier.addListener(view, view::notifyPlayerTurn);
        this.playerLostNotifier.addListener(view, view::notifyPlayerDefeat);
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

    public boolean isGodAvailable(GodType type) {
        return this.availableGods.contains(type);
    }

    public God chooseGod(GodType type) {
        if (this.status != GameStatus.CHOOSING_GODS) {
            throw new InternalError("Cannot choose available gods now");
        }
        if (availableGods == null) {
            throw new InternalError("The Challenger has not chosen the Gods available for this game yet");
        }
        if (!this.isGodAvailable(type)) {
            throw new InternalError("God" + type + " is not available");
        }
        this.availableGods.remove(type);
        this.availableGodsNotifier.notify(this.availableGods);
        return factory.getGod(type);
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
        this.setCurrentPlayer((this.currentPlayer + 1) % this.listOfPlayers.size());
    }

    // solo per i test //
    public int getCurrentPlayerIndex() {
        return currentPlayer;
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

    public int getTurnPhase() {
        return this.world.getNumberOfSavedPreviousWorlds();
    }

    public void clearPreviousWorlds() {
        this.world.clearPreviousWorlds();
    }

    public WorldData getPreviousWorld() {
        return this.world.peekPrevious().orElseThrow(() -> {
            //TODO: handle empty previousWorlds
            return new UnsupportedOperationException("Empty World error non implemented yet!");
        });
    }

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

    public void setCurrentPlayer(int i) {
        this.currentPlayer = i;
        this.turnChangedNotifier.notify(this.getCurrentPlayer().getName());
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

    public Map<WorkerActionType, List<Vector2>> workerActionTypeListMap() {
        Map<WorkerActionType, List<Vector2>> actions = new HashMap<>();
        Worker selectedWorker = this.getCurrentPlayer().getSelectedWorker();
        List<WorkerActionType> possibleActions = this.getRules().possibleActions(this.getTurnPhase(), selectedWorker);
        for (WorkerActionType w : possibleActions) {
            if (w == WorkerActionType.MOVE) {
                List<Vector2> availableSpacesCoordinates = new ArrayList<>();
                List<Space> availableSpaces = selectedWorker.computeAvailableSpaces();
                availableSpaces.forEach(s -> availableSpacesCoordinates.add(s.getCoordinates()));
                actions.put(w, availableSpacesCoordinates);
            } else if (w == WorkerActionType.BUILD) {
                List<Vector2> buildableSpacesCoordinates = new ArrayList<>();
                List<Space> buildableSpaces = selectedWorker.computeBuildableSpaces();
                buildableSpaces.forEach(s -> buildableSpacesCoordinates.add(s.getCoordinates()));
                actions.put(w, buildableSpacesCoordinates);
            } else if (w == WorkerActionType.BUILD_DOME) {
                List<Vector2> buildDomeSpacesCoordinates = new ArrayList<>();
                List<Space> buildDomeSpaces = selectedWorker.computeDomeSpaces();
                buildDomeSpaces.forEach(s -> buildDomeSpacesCoordinates.add(s.getCoordinates()));
                actions.put(w, buildDomeSpacesCoordinates);
            } else if (w == WorkerActionType.END_TURN) {
                actions.put(w, null);
            }
        }
        if (!(actions.keySet().size() == 1 && actions.containsKey(WorkerActionType.END_TURN))) {
            if (this.checkDefeat(actions)) {
                this.announceDefeat(this.getCurrentPlayer());
            }
        }
        return actions;
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

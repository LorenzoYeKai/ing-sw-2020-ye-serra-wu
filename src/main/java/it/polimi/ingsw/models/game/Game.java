package it.polimi.ingsw.models.game;

import it.polimi.ingsw.Notifier;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.InternalError;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.ActualRule;

import it.polimi.ingsw.server.GameRemoteView;
import it.polimi.ingsw.views.game.GameView;
import it.polimi.ingsw.views.utils.Coordinates;


import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {
    //private final Notifier<GameStatus> gameStatusNotifier;
    private final Notifier<SpaceData> spaceChangedNotifier;
    private final Notifier<PlayerData> turnChangedNotifier;
    private final Notifier<PlayerData> playerLostNotifier;
    private final Notifier<PlayerData> playerWonNotifier;


    private final GodFactory factory;
    private Set<GodType> availableGods;
    private World world;
    private World previousWorld; //TODO: delete this.previousWorld
    private List<World> previousWorlds;
    private ActualRule rules;





    private final List<Player> listOfPlayers;
    private final Map<String, GameView> gameViews;
    private final Map<String, GameRemoteView> gameRemoteViews;

    private GameStatus currentStatus;
    private int currentPlayer;
    private GameStatus status;

    /**
     * Creates a new game
     *
     * @param nicknames the nicknames of the players
     */
    public Game(List<String> nicknames) {
        //this.gameStatusNotifier = new Notifier<>();
        this.spaceChangedNotifier = new Notifier<>();
        this.turnChangedNotifier = new Notifier<>();
        this.playerLostNotifier = new Notifier<>();
        this.playerWonNotifier = new Notifier<>();

        this.factory = new GodFactory();
        this.availableGods = new HashSet<GodType>();
        this.world = new World(this.spaceChangedNotifier);
        this.previousWorld = null;
        this.previousWorlds = new ArrayList<>();
        this.rules = new ActualRule(this.world);


        this.listOfPlayers = new ArrayList<Player>();
        nicknames.forEach(n -> listOfPlayers.add(new Player(this, n)));
        this.gameViews = new HashMap<>();
        this.gameRemoteViews = new HashMap<>();

        this.currentPlayer = -1;
        this.currentStatus = GameStatus.PLAYER_JOINING;
        this.status = GameStatus.PLAYER_JOINING;
    }

    public void oldAttachView(String name, GameView view) {
        if(this.gameViews.containsKey(name)) {
            throw new InternalError("Player already exist");
        }

        //this.gameStatusNotifier.addListener(view, view::notifyGameStatus);
        this.spaceChangedNotifier.addListener(view, view::notifySpaceChange);
        this.turnChangedNotifier.addListener(view, view::notifyPlayerTurn);
        //this.playerLostNotifier.addListener(view, view::notifyPlayerDefeat);
    }

    public void attachView(String name, GameRemoteView view){
        if(this.gameViews.containsKey(name)) {
            throw new InternalError("Player already exist");
        }
        this.gameRemoteViews.put(name, view);
        this.playerLostNotifier.addListener(view, view::playerDefeatMessage);
        this.playerWonNotifier.addListener(view, view::playerVictoryMessage);
    }

    public void detachView(String name, GameView view) {
        if(!this.gameViews.containsKey(name)) {
            throw new InternalError("Invalid player name");
        }

        //this.gameStatusNotifier.removeListener(view);
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
     */
    public void addAvailableGods(GodType type) {
        if (this.availableGods.size() == getNumberOfActivePlayers()) {
            throw new InternalError("Available gods already set");
        }
        if (this.availableGods.contains(type)) {
            throw new InternalError("Challenger may have selected duplicated god types");
        }
        this.availableGods.add(type);

    }

    public void removeAvailableGod(GodType type){
        if(this.availableGods.contains(type)){
            this.availableGods.remove(type);
        }
        else{
            throw new IllegalArgumentException("The god is not available");
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

    public void announceVictory(Player winner) {
        /*for (Player player : this.listOfPlayers) {
            if (player != winner) {
                this.announceDefeat(player);
            }
        }*/
        this.playerWonNotifier.notify(winner);
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
        return this.listOfPlayers.get(this.currentPlayer);
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
        this.currentPlayer = ((this.currentPlayer + 1) % this.listOfPlayers.size());
        this.turnChangedNotifier.notify(this.getCurrentPlayer());
    }

    // solo per i test //
    public int getCurrentPlayerIndex(){
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

    public GameStatus getCurrentStatus() {
        return this.currentStatus;
    }

    public void setCurrentStatus(GameStatus status) {
        this.currentStatus = status;
        //this.gameStatusNotifier.notify(this.currentStatus);
    }

    public void savePreviousWorld(){
        this.previousWorld = new World(this.world);
        this.previousWorlds.add(this.previousWorld);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Worker w = this.world.getSpaces(i, j).getWorker();
                if(w != null){
                    this.previousWorld.getSpaces(i, j).setWorker(new Worker(w));
                }
            }
        }

    }

    public int getTurnPhase(){
        return this.previousWorlds.size();
    }

    public void clearPreviousWorlds(){
        this.previousWorld = null;
        this.previousWorlds.clear();
    }

    public World getPreviousWorld(){
        if(this.previousWorlds.isEmpty()){
            throw new UnsupportedOperationException("Empty World error non implemented yet!"); //TODO: handle empty previousWorlds
        }
        return this.previousWorlds.get(this.previousWorlds.size() - 1);
    }

    public void gameUndo(){
        World previousWorld = this.getPreviousWorld();
        this.previousWorlds.remove(previousWorld);
        this.world = previousWorld;
    }

    public List<PlayerData> getPlayerData(){
        return new ArrayList<>(this.listOfPlayers);
    }

    public boolean availableGodsContains(GodType type){
        return availableGods.contains(type);
    }

    public Set<GodType> getAvailableGods(){
        return this.availableGods;
    }

    public void setCurrentPlayer(int i){
        this.currentPlayer = i;
    }

    public void nextPlayer(){
        switch (currentPlayer){
            case 2 : setCurrentPlayer(0);
            case 1 : setCurrentPlayer(2);
            case 0 : setCurrentPlayer(1);
        }
    }

    public List<Player> getListOfPlayers() {
        return listOfPlayers;
    }

    public void setStatus(GameStatus status){
        this.status = status;
    }

    public GameStatus getStatus(){
        return this.status;
    }

    public Map<WorkerActionType, List<Coordinates>> workerActionTypeListMap(){
        Map<WorkerActionType, List<Coordinates>> actions = new HashMap<>();
        Worker selectedWorker = this.getCurrentPlayer().getSelectedWorker();
        List<WorkerActionType> possibleActions = this.getRules().possibleActions(this.getTurnPhase(), selectedWorker);
        for(WorkerActionType w : possibleActions){
            if(w == WorkerActionType.MOVE) {
                List<Coordinates> availableSpacesCoordinates = new ArrayList<>();
                List<Space> availableSpaces = selectedWorker.computeAvailableSpaces();
                availableSpaces.forEach(s -> availableSpacesCoordinates.add(s.getCoordinates()));
                actions.put(w, availableSpacesCoordinates);
            }
            else if(w == WorkerActionType.BUILD) {
                List<Coordinates> buildableSpacesCoordinates = new ArrayList<>();
                List<Space> buildableSpaces = selectedWorker.computeBuildableSpaces();
                buildableSpaces.forEach(s -> buildableSpacesCoordinates.add(s.getCoordinates()));
                actions.put(w, buildableSpacesCoordinates);
            }
            else if(w == WorkerActionType.BUILD_DOME) {
                List<Coordinates> buildDomeSpacesCoordinates = new ArrayList<>();
                List<Space> buildDomeSpaces = selectedWorker.computeDomeSpaces();
                buildDomeSpaces.forEach(s -> buildDomeSpacesCoordinates.add(s.getCoordinates()));
                actions.put(w, buildDomeSpacesCoordinates);
            }
            else if(w == WorkerActionType.END_TURN){
                actions.put(w, null);
            }
        }
        if(!(actions.keySet().size() == 1 && actions.containsKey(WorkerActionType.END_TURN))){
            if(this.checkDefeat(actions)){
                this.announceDefeat(this.getCurrentPlayer());
            }
        }
        return actions;
    }

    private boolean checkDefeat(Map<WorkerActionType, List<Coordinates>> actions){
        for(WorkerActionType w : actions.keySet()){
            if(w != WorkerActionType.END_TURN){
                if(!actions.get(w).isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }

}

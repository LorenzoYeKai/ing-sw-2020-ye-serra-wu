package it.polimi.ingsw.controller.game;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.server.GameRemoteView;
import it.polimi.ingsw.views.game.GameView;

import java.util.List;

public class GameController {
    private final Game game;
    private final ActualRule rules;
    private boolean gameStarted = false;

    public GameController(List<String> nicknames) {
        this.game = new Game(nicknames);
        this.rules = this.game.getRules();
    }

    public PlayerData joinGame(String nickname, GameView view) { //Old method for the local game view
        this.game.oldAttachView(nickname, view);
        return this.game.findPlayerByName(nickname);
    }

    public void bindGameRemoteView(String nickname, GameRemoteView view){
        this.game.attachView(nickname, view);
    }

    // TODO: call worker.startTurn() somewhere
    public void workerAction(WorkerData workerData,
                             WorkerActionType action,
                             int x, int y) throws NotExecutedException {
        if (workerData.getPlayer().isDefeated()) {
            throw new NotExecutedException("You have been defeated");
        }
        if (workerData.getPlayer() != this.game.getCurrentPlayer()) {
            throw new NotExecutedException("Not your turn");
        }

        Worker worker = this.game.getCurrentPlayer().getWorker(workerData);
        Space targetSpace = this.game.getWorld().getSpaces(x, y);

        if (action == WorkerActionType.PLACE && this.gameStarted) {
            throw new NotExecutedException("Game already started");
        }
        if (action != WorkerActionType.PLACE && !this.gameStarted) {
            throw new NotExecutedException("Game not started yet");
        }

        switch (action) {
            case PLACE:   //Da togliere
                this.place(worker, targetSpace);
                break;
            case MOVE:
                this.move(worker, targetSpace);
                break;
            case BUILD:
                this.build(worker, targetSpace);
                break;
            case BUILD_DOME:
                this.buildDome(worker, targetSpace);
                break;
        }
    }

    public void addAvailableGods(GodType type){
        game.addAvailableGods(type);
    }

    public void removeAvailableGod(GodType type){
        game.removeAvailableGod(type);
    }

    public void nextTurn(){
        if(game.getCurrentPlayer().getIndex()==game.getNumberOfActivePlayers()-1) {
            game.setCurrentPlayer(0);
        }
        else{
            game.setCurrentPlayer(game.getCurrentPlayer().getIndex() + 1);
        }
    }

    public void setCurrentPlayer(int index){   // l'indice del giocatore lo prendiamo lato client

        game.setCurrentPlayer(index);

    }

    public void place(Worker worker, Space targetSpace) throws NotExecutedException {
        if (targetSpace.isOccupied()) {
            throw new NotExecutedException("Cannot place in an occupied space!");
        }

        worker.setStartPosition(targetSpace);
    }

    public void move(Worker worker, Space targetSpace) throws NotExecutedException {
        if (!worker.computeAvailableSpaces().contains(targetSpace)){
            throw new NotExecutedException("Cannot move there!");
        }
        if(targetSpace.isOccupiedByWorker()){
            try {
                game.savePreviousWorld();
                worker.getPlayer().getGod().forcePower(worker, targetSpace);
            } catch (UnsupportedOperationException e){
                System.err.println(e.getMessage());
            }
        }
        else {
            game.savePreviousWorld();
            worker.move(targetSpace);
        }

    }

    public void build(Worker worker, Space targetSpace) throws NotExecutedException {
        if (!worker.computeBuildableSpaces().contains(targetSpace)) {
            throw new NotExecutedException("Cannot build there!");
        }
        game.savePreviousWorld();
        worker.buildBlock(targetSpace);
    }

    public void buildDome(Worker worker, Space targetSpace) throws NotExecutedException {
        if (!worker.computeDomeSpaces().contains(targetSpace)) {
            throw new NotExecutedException("Cannot build a dome there!");
        }
        game.savePreviousWorld();
        worker.buildDome(targetSpace);
    }

    public void handleDefeat(Player player){
        if(!(player.getIndex() == game.getNumberOfActivePlayers()-1)){
            nextTurn();
        }
        game.getListOfPlayers().remove(player);
        player.getAllWorkers().get(0).removeWorkerWhenDefeated();
        player.getAllWorkers().get(1).removeWorkerWhenDefeated();

    }

    public void checkDefeat(WorkerActionType type, Worker worker){
        switch(type){
            case MOVE:
                if(worker.computeAvailableSpaces().size()==0) {
                    worker.getPlayer().setDefeated();
                }
            case BUILD  :
                if(worker.computeBuildableSpaces().size()==0 && worker.computeDomeSpaces().size()==0) {
                    worker.getPlayer().setDefeated();
                }
            case BUILD_DOME:
                if(worker.computeBuildableSpaces().size()==0 && worker.computeDomeSpaces().size()==0) {
                    worker.getPlayer().setDefeated();
                }
        }
    }

    public Game getGame(){
        return this.game;
    }

    public void setupGame() {
        this.game.setStatus(GameStatus.SETUP);
    }

    public void chooseGods(){
        this.game.setStatus(GameStatus.CHOOSING_GODS);
    }

    public void placeWorkers(){
        this.game.setStatus(GameStatus.PLACING);
    }

    public void playGame() {
        this.game.setStatus(GameStatus.PLAYING);
    }



    public void setPlayerGod(Player player, GodType god){
        player.setGod(god);
    }

    public void selectWorker(int index){
        game.getCurrentPlayer().selectWorker(index);
    }

    public void resetTurn(){
        this.game.clearPreviousWorlds();
        this.game.getCurrentPlayer().deselectWorker();
    }
}

package it.polimi.ingsw.controller.game;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.views.game.GameView;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocalGameController implements GameController {
    protected final Game game;
    private final ActualRule rules;
    private boolean gameStarted = false;

    public LocalGameController(List<String> nicknames) {
        this.game = new Game(nicknames);
        this.rules = this.game.getRules();
    }

    public void joinGame(String nickname, GameView view) { //Old method for the local game view
        this.game.attachView(nickname, view);
        //return this.game.findPlayerByName(nickname);
    }

    public void selectWorker(int index) {
        game.getCurrentPlayer().selectWorker(index);
        // this will be useful to make the game predict actions
        game.clearCurrentWorkerMovedFlag();
        game.calculateValidWorkerActions();
    }

    public void workerAction(String player,
                             WorkerActionType action,
                             int x, int y) throws NotExecutedException {
        // should be not checked because checking currentPlayer is enough
        /*if (workerData.getPlayer().isDefeated()) {
            throw new NotExecutedException("You have been defeated");
        }*/
        if (!this.game.getCurrentPlayer().getName().equals(player)) {
            throw new NotExecutedException("Not your turn");
        }

        if(!this.game.getCurrentPlayer().hasSelectedAWorker()) {
            if(action != WorkerActionType.PLACE) {
                throw new NotExecutedException("You need to select worker first");
            }
        }

        if(action == WorkerActionType.PLACE){
            game.getCurrentPlayer().selectWorker(0);
            if(game.getCurrentPlayer().getSelectedWorker().getCurrentSpace() != null){
                game.getCurrentPlayer().selectWorker(1);
            }
        }
        Worker worker = this.game.getCurrentPlayer().getSelectedWorker();
        Space targetSpace = this.game.getWorld().get(x, y);

        if (action == WorkerActionType.PLACE && this.gameStarted) {
            throw new NotExecutedException("Game already started");
        }
        if (action != WorkerActionType.PLACE && !this.gameStarted) {
            throw new NotExecutedException("Game not started yet");
        }

        if (action != WorkerActionType.PLACE) {
            // check if the request action is valid
            if(!this.getValidActions().containsKey(action)) {
                // this action isn't valid
                throw new NotExecutedException("Invalid action");
            }
            if(!this.getValidActions().get(action).contains(new Vector2(x, y))) {
                // the action is valid, but not valid with this coordinate.
                throw new NotExecutedException("Invalid action");
            }
        }

        switch (action) {
            case PLACE -> this.place(worker, targetSpace);
            case MOVE -> this.move(worker, targetSpace);
            case WIN -> this.move(worker, targetSpace);
            case BUILD -> this.build(worker, targetSpace);
            case BUILD_DOME -> this.buildDome(worker, targetSpace);
        }

        if (action == WorkerActionType.MOVE) {
            this.game.setCurrentWorkerMovedFlag();
        }
        this.game.calculateValidWorkerActions();
    }

    public void addAvailableGods(GodType type) {
        game.addAvailableGods(type);
    }

    public void removeAvailableGod(GodType type) {
        game.removeAvailableGod(type);
    }

    public void nextTurn() {
        if (game.getCurrentPlayer().getIndex() == game.getNumberOfActivePlayers() - 1) {
            game.setCurrentPlayer(0);
        } else {
            game.setCurrentPlayer(game.getCurrentPlayer().getIndex() + 1);
        }
    }

    public void setCurrentPlayer(int index) {   // l'indice del giocatore lo prendiamo lato client
        game.setCurrentPlayer(index);
    }

    public void place(Worker worker, Space targetSpace) throws NotExecutedException {
        if (targetSpace.isOccupied()) {
            throw new NotExecutedException("Cannot place in an occupied space!");
        }

        worker.setStartPosition(targetSpace);
    }

    public void move(Worker worker, Space targetSpace) throws NotExecutedException {
        if (!worker.computeAvailableSpaces().contains(targetSpace) &&
                !worker.computeWinSpaces().contains(targetSpace)) {
            throw new NotExecutedException("Cannot move there!");
        }
        if (targetSpace.isOccupiedByWorker()) {
            worker.getPlayer().getGod().forcePower(worker, targetSpace);
        } else {
            worker.move(targetSpace);
        }

    }

    public void build(Worker worker, Space targetSpace) throws NotExecutedException {
        if (!worker.computeBuildableSpaces().contains(targetSpace)) {
            throw new NotExecutedException("Cannot build there!");
        }
        worker.buildBlock(targetSpace);
    }

    public void buildDome(Worker worker, Space targetSpace) throws NotExecutedException {
        if (!worker.computeDomeSpaces().contains(targetSpace)) {
            throw new NotExecutedException("Cannot build a dome there!");
        }
        worker.buildDome(targetSpace);
    }

    public void handleDefeat(Player player) {
        if (!(player.getIndex() == game.getNumberOfActivePlayers() - 1)) {
            nextTurn();
        }
        game.getListOfPlayers().remove(player);
        player.getAllWorkers().get(0).removeWorkerWhenDefeated();
        player.getAllWorkers().get(1).removeWorkerWhenDefeated();

    }

    public void checkDefeat(WorkerActionType type, Worker worker) {
        throw new InternalError("Not implemented");
    }

    @Override
    public void setGameStatus(GameStatus status) {
        this.game.setStatus(status);
    }

    public void setPlayerGod(String player, GodType god) throws NotExecutedException {
        Optional<Player> found = this.game.getListOfPlayers().stream()
                .filter(p -> p.getName().equals(player)).findAny();
        if (found.isEmpty()) {
            throw new NotExecutedException("No such player");
        }
        if (!this.game.isGodAvailable(god)) {
            throw new NotExecutedException("This god is not available");
        }
        this.game.chooseGod(found.get(), god);
    }

    public void resetTurn() {
        this.game.clearPreviousWorlds();
        this.game.getCurrentPlayer().deselectWorker();
        this.game.clearCurrentWorkerMovedFlag();
    }

    public void undo() {
        //TODO: some god may have the power activated after the undo!!!
        game.gameUndo();
    }

    @Override
    public Map<WorkerActionType, List<Vector2>> getValidActions()
            throws NotExecutedException {
        if (!this.game.getCurrentPlayer().hasSelectedAWorker()) {
            throw new NotExecutedException("Select a worker first!");
        }
        return this.game.getValidWorkerActions();
    }
}

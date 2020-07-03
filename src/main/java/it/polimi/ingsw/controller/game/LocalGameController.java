package it.polimi.ingsw.controller.game;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.views.game.GameView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocalGameController implements GameController {
    protected final Game game;
    private final ActualRule rules;
    private final List<WorkerActionType> currentActions;


    public LocalGameController(List<String> nicknames) {
        this.game = new Game(nicknames);
        this.rules = this.game.getRules();
        this.currentActions = new ArrayList<>();
    }

    public void joinGame(String nickname, GameView view) {
        this.game.attachView(nickname, view);
    }

    public void selectWorker(int index) {
        this.game.getCurrentPlayer().selectWorker(index);
        this.currentActions.clear();
        // this will be useful to make the game predict actions
        this.game.clearCurrentWorkerMovedFlag();
        this.game.calculateValidWorkerActions();
    }

    public void workerAction(String player,
                             WorkerActionType action,
                             int x, int y) throws NotExecutedException {

        if (!this.game.getCurrentPlayer().getName().equals(player)) {
            throw new NotExecutedException("Not your turn");
        }

        if (!this.game.getCurrentPlayer().hasSelectedAWorker()) {
            if (action != WorkerActionType.PLACE) {
                throw new NotExecutedException("You need to select worker first");
            }
        }

        /*if (action == WorkerActionType.PLACE) {
            game.getCurrentPlayer().selectWorker(0);
            if (game.getCurrentPlayer().getSelectedWorker().getCurrentSpace() != null) {
                game.getCurrentPlayer().selectWorker(1);
            }
        }*/
        Worker worker = this.game.getCurrentPlayer().getSelectedWorker();
        Space targetSpace = this.game.getWorld().get(x, y);

        if (this.game.getStatus() != GameStatus.PLACING && action == WorkerActionType.PLACE) {
            throw new NotExecutedException("Cannot place now");
        }
        if (this.game.getStatus() == GameStatus.PLACING && action != WorkerActionType.PLACE) {
            throw new NotExecutedException("You must place now");
        }

        if (action != WorkerActionType.PLACE) {
            // check if the request action is valid
            if (!this.getValidActions().containsKey(action)) {
                // this action isn't valid
                throw new NotExecutedException("Invalid action");
            }
            if (!this.getValidActions().get(action).contains(new Vector2(x, y))) {
                // the action is valid, but not valid with this coordinate.
                throw new NotExecutedException("Invalid action");
            }
        }

        switch (action) {
            case PLACE -> this.place(worker, targetSpace);
            case MOVE, WIN -> this.move(worker, targetSpace);
            case BUILD -> this.build(worker, targetSpace);
            case BUILD_DOME -> this.buildDome(worker, targetSpace);
        }

        this.currentActions.add(action);
        if (action == WorkerActionType.MOVE) {
            this.game.setCurrentWorkerMovedFlag();
        }
        this.game.calculateValidWorkerActions();
    }

    public void addAvailableGods(GodType type) throws NotExecutedException {
        if (this.game.isGodAvailable(type)) {
            throw new NotExecutedException("God already added");
        }
        this.game.addAvailableGods(type);
    }

    public void removeAvailableGod(GodType type) throws NotExecutedException {
        if (!this.game.isGodAvailable(type)) {
            throw new NotExecutedException("Cannot remove non existing god");
        }
        this.game.removeAvailableGod(type);
    }

    public void nextTurn() throws NotExecutedException {
        if (this.game.getStatus() == GameStatus.PLACING ||
                this.game.getStatus() == GameStatus.BEFORE_PLAYING) {
            // ensure all workers has been placed

            for (Worker worker : this.game.getCurrentPlayer().getAllWorkers()) {
                if (worker.getCurrentSpace() == null) {
                    throw new NotExecutedException("You need to place all your workers!");
                }
            }
        }
        else if (this.game.getStatus() == GameStatus.PLAYING) {
            // ensure move then build
            int buildBlockIndex = this.currentActions.lastIndexOf(WorkerActionType.BUILD);
            int buildDomeIndex = this.currentActions.lastIndexOf(WorkerActionType.BUILD_DOME);
            int buildIndex = Integer.max(buildBlockIndex, buildDomeIndex);
            if(!this.currentActions.contains(WorkerActionType.MOVE)) {
                throw new NotExecutedException("You must move at least once before ending the turn");
            }
            if(buildIndex < this.currentActions.indexOf(WorkerActionType.MOVE)) {
                throw new NotExecutedException("You must move then build before ending the turn");
            }
        }
        this.game.goToNextTurn();
        this.resetTurn();
    }

    public void setCurrentPlayer(int index) {
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

    @Override
    public void setGameStatus(GameStatus status)  {
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

    public void undo() throws NotExecutedException {
        if(this.currentActions.isEmpty()) {
            throw new NotExecutedException("Cannot undo because you haven't done anything yet");
        }
        this.game.gameUndo();
        this.currentActions.remove(this.currentActions.size() - 1);
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

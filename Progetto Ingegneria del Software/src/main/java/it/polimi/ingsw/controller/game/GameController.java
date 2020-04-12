package it.polimi.ingsw.controller.game;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.models.lobby.UserData;
import it.polimi.ingsw.views.game.GameView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameController {
    private final Game game;
    private final ActualRule rules;
    private boolean gameStarted = false;

    public GameController(List<String> nicknames) {
        this.game = new Game(nicknames);
        this.rules = this.game.getRules();
    }

    public PlayerData joinGame(String nickname, GameView view) {
        this.game.attachView(nickname, view);
        return this.game.findPlayerByName(nickname);
    }

    public void playGame() {
        this.game.setupGame();
        this.gameStarted = true;
        this.game.playGame();
    }

    public void workerAction(WorkerData workerData,
                             WorkerActionType action,
                             int x, int y) throws NotExecutedException {
        if (workerData.getPlayer().isDefeated()) {
            throw new NotExecutedException("You have been defeated");
        }
        if (workerData.getPlayer() != this.game.getCurrentPlayer()) {
            throw new NotExecutedException("Not your turn");
        }

        Space targetSpace = this.game.getWorld().getSpaces(x, y);

        if(action == WorkerActionType.PLACE) {
            this.place(workerData, targetSpace);
            return;
        }

        if(!this.gameStarted) {
            throw new NotExecutedException("Game not started yet");
        }

        List<Worker> workers = this.game.getCurrentPlayer().getAllWorkers();
        int index = workers.indexOf(workerData);
        if (index == -1) {
            throw new NotExecutedException("Invalid worker");
        }
        Worker worker = workers.get(index);

        switch (action) {
            case MOVE:
                this.move(worker, targetSpace);
                break;
            case BUILD:
                this.build(worker, targetSpace);
                break;
            case BUILD_DOME:
                this.buildDome(worker, targetSpace);
        }
    }

    private void place(WorkerData workerData, Space targetSpace) throws NotExecutedException {
        if(this.gameStarted) {
            throw new NotExecutedException("Game already started");
        }

        if (targetSpace.isOccupied()) {
            throw new NotExecutedException("Cannot move to occupied space");
        }

        List<Worker> workers = this.game.getCurrentPlayer().getAllWorkers();
        int index = workers.indexOf(workerData);
        if (index == -1) {
            throw new NotExecutedException("Invalid worker");
        }
        Worker worker = workers.get(index);

        worker.move(targetSpace);
    }

    private void move(Worker worker, Space targetSpace) throws NotExecutedException {
        Space workerSpace = this.game.getWorld().getSpaces(worker.getX(), worker.getY());
        if(!this.rules.canMoveThere(workerSpace, targetSpace)) {
            throw new NotExecutedException("Cannot move there");
        }
        worker.move(targetSpace);
    }

    private void build(Worker worker, Space targetSpace) throws NotExecutedException {
        Space workerSpace = this.game.getWorld().getSpaces(worker.getX(), worker.getY());
        if(!this.rules.canBuildThere(workerSpace, targetSpace)) {
            throw new NotExecutedException("Cannot build there");
        }

        worker.buildBlock(targetSpace);
    }

    private void buildDome(Worker worker, Space targetSpace) throws NotExecutedException {
        Space workerSpace = this.game.getWorld().getSpaces(worker.getX(), worker.getY());
        if(!this.rules.canBuildDomeThere(workerSpace, targetSpace)) {
            throw new NotExecutedException("Cannot build dome there");
        }

        worker.buildDome(targetSpace);
    }
}

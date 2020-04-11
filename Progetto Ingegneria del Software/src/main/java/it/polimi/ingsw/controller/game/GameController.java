package it.polimi.ingsw.controller.game;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.lobby.UserData;
import it.polimi.ingsw.views.game.GameView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameController {
    private final Game game;
    private boolean gameStarted = false;

    public GameController(List<String> nicknames) {
        this.game = new Game(nicknames);
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


        List<Worker> workers = this.game.getCurrentPlayer().getAllWorkers();
        int index = workers.indexOf(workerData);
        if (index == -1) {
            throw new NotExecutedException("Invalid worker");
        }
        Worker worker = workers.get(index);

        Space targetSpace = this.game.getWorld().getSpaces(x, y);

        // need to check if this worker can move

        switch (action) {
            case BUILD:
                this.build(worker, targetSpace);
                break;
            case MOVE:
                this.move(worker, targetSpace);
                break;
        }
    }

    // TODO: IMPLEMENT RULES / DIVINITIES
    private void build(Worker worker, Space targetSpace) throws NotExecutedException {
        if (!this.gameStarted) {
            throw new NotExecutedException("Cannot build now");
        }

        if (targetSpace.isOccupiedByDome()) {
            throw new NotExecutedException("Cannot build on dome");
        }

        Space workerSpace = this.game.getWorld().getSpaces(worker.getX(), worker.getY());
        if (!workerSpace.isNeighbor(targetSpace)) {
            throw new NotExecutedException("Cannot build there");
        }

        if (targetSpace.getLevel() == 3) {
            worker.buildDome(targetSpace);
        } else {
            worker.buildBlock(targetSpace);
        }
    }

    private void move(Worker worker, Space targetSpace) throws NotExecutedException {
        if (targetSpace.isOccupied()) {
            throw new NotExecutedException("Cannot move to occupied space");
        }


        if (this.gameStarted) {
            // Check this only when the game has started
            // So we allow workers to set position during setup phase

            Space workerSpace = this.game.getWorld().getSpaces(worker.getX(), worker.getY());
            if (!workerSpace.isNeighbor(targetSpace)) {
                throw new NotExecutedException("Cannot move there");
            }
        }


        worker.move(targetSpace);
    }

}

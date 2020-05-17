package it.polimi.ingsw.controller.game;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.rules.ActualRule;
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

    public PlayerData joinGame(String nickname, GameView view) {
        this.game.attachView(nickname, view);
        return this.game.findPlayerByName(nickname);
    }

    public void playGame() {
        this.game.setupGame();
        this.gameStarted = true;
        this.game.playGame();
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
        }
    }

    public void place(Worker worker, Space targetSpace) throws NotExecutedException {
        if (targetSpace.isOccupied()) {
            throw new NotExecutedException("Cannot move to occupied space");
        }

        worker.setStartPosition(targetSpace);
    }

    private void move(Worker worker, Space targetSpace) throws NotExecutedException {
        if (!worker.computeAvailableSpaces().contains(targetSpace)){
            throw new NotExecutedException("Cannot move there");
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

    private void build(Worker worker, Space targetSpace) throws NotExecutedException {
        if (!worker.computeBuildableSpaces().contains(targetSpace)) {
            throw new NotExecutedException("Cannot build there");
        }
        game.savePreviousWorld();
        worker.buildBlock(targetSpace);
    }

    private void buildDome(Worker worker, Space targetSpace) throws NotExecutedException {
        if (!worker.computeDomeSpaces().contains(targetSpace)) {
            throw new NotExecutedException("Cannot build dome there");
        }
        game.savePreviousWorld();
        worker.buildDome(targetSpace);
    }
}

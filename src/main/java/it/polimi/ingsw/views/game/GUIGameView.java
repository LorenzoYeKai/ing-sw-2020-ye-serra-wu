package it.polimi.ingsw.views.game;

import it.polimi.ingsw.GUI.GameGUIController;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Vector2;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.gods.GodType;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIGameView implements GameView {

    private final String player;
    private final GameController controller;
    private GameGUIController gameGUIController;
    private final Space[] spaces = new Space[World.SIZE * World.SIZE];
    private final List<String> allPlayers;
    private final Map<String, GodType> playerGods;
    private GameStatus currentStatus = GameStatus.SETUP;

    public GUIGameView(String player,
                       List<String> allPlayers,
                       GameController controller,
                       GameGUIController gameGUIController) {
        this.player = player;
        this.controller = controller;
        this.gameGUIController = gameGUIController;
        this.allPlayers = allPlayers;
        this.playerGods = new HashMap<>();
    }


    public GameStatus getCurrentStatus(){
        return this.currentStatus;
    }

    @Override
    public void notifyGameStatus(GameStatus status) {
        System.out.println("I'm in notifyGameStatus, current status: " + status.toString());
        this.currentStatus = status;
    }

    @Override
    public void notifyAvailableGods(Collection<GodType> availableGods) {
        gameGUIController.addAvailableGods(availableGods);
    }

    @Override
    public void notifyPlayerGods(Map<String, GodType> playerAndGods) {

        playerAndGods.forEach((p, g) -> gameGUIController.displayOpponentsAvailableGods(p, g));
    }


    @Override
    public void notifySpaceChange(Space space) {
        gameGUIController.updateWorld(space);
    }

    @Override
    public void notifyPlayerTurn(String player) {

        if(player.equals(this.player)) {
            gameGUIController.initCurrentTurn(this.currentStatus);
        }
        else{
            gameGUIController.notYourTurnMessage(this.currentStatus, player);
        }
    }

    @Override
    public void notifyPlayerDefeat(String player) {

    }

    public void executeAction(String line) throws NotExecutedException, IOException {
        switch (line) {
            case "join" -> controller.joinGame(player, this);
            case "challengerChoice" -> {
                this.gameGUIController.getAvailableGodsChoseByTheChallenger().forEach(g -> {
                    try {
                        this.controller.addAvailableGods(g);
                    } catch (NotExecutedException | IOException e) {
                        e.printStackTrace();
                    }
                });
                this.controller.setGameStatus(GameStatus.CHOOSING_GODS);
                this.controller.setCurrentPlayer(gameGUIController.getListOfPlayers().indexOf(gameGUIController.getFirstPlayerName()));
            }
            case "placing" -> this.controller.setGameStatus(GameStatus.PLACING);
            case "place" -> this.controller.workerAction(player, WorkerActionType.PLACE, gameGUIController.getSelectedX(), gameGUIController.getSelectedY());
            case "play" -> this.controller.setGameStatus(GameStatus.BEFORE_PLAYING);
            case "end" -> this.controller.nextTurn();
            case "setup" -> this.controller.setGameStatus(GameStatus.SETUP);
            case "god" -> this.controller.setPlayerGod(this.player, gameGUIController.getChosenGod());
            case "select" -> this.controller.selectWorker(gameGUIController.getSelectedWorkerIndex());
            case "validate0" -> {
                Map<WorkerActionType, List<Vector2>> possibleActions = this.controller.getValidActions();
                this.gameGUIController.setWorkerOnePossibleActions(possibleActions);
            }
            case "validate1" -> {
                Map<WorkerActionType, List<Vector2>> possibleActions = this.controller.getValidActions();
                this.gameGUIController.setWorkerTwoPossibleActions(possibleActions);
            }
            //case "SELECT" -> this.controller.selectWorker(scanner.nextInt());*/
        }
    }
}

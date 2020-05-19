package it.polimi.ingsw.server;

import it.polimi.ingsw.Notifier;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodType;

import java.util.Set;

/**
 * Now is possible for the challenger to chooses the available gods and display to only to the current player the option to choose the god
 */
public class GameRemoteView {

    private GameServer gameServer;
    private Game game;
    private GameController controller;
    private Server server;



    public GameRemoteView(GameServer gameServer, Game game, GameController controller, Server server){
        this.gameServer = gameServer;
        this.game = game;
        this.controller = controller;
        this.server = server;


    }

    public void stringHandler(String message){
        System.out.println(message);
        if(game.getStatus().equals(GameStatus.SETUP)) {
            setup(message);
        }
        else if(game.getStatus().equals(GameStatus.CHOOSING_GODS)){
            chooseGods(message);
            // TODO: Display message with the remaining gods
        }

    }

    public void setupMessage(GameSetup setup){
        gameServer.asyncSend(setup);
        if(gameServer.isChallenger()){
            gameServer.asyncSend("You are the challenger choose 3 gods and the first player: ");
        }
        else{
            gameServer.asyncSend("You are NOT the challenger, wait for the challenger's choices");
        }
    }

    public void chooseGodsMessage(AvailableGodsChoice availableGods){
        if(gameServer.isCurrentPlayer()){
            gameServer.asyncSend("You are the current Player choose your God: ");
            gameServer.asyncSend(availableGods);
        }
        else{
            gameServer.asyncSend("You are NOT the current Player, wait for " + game.getCurrentPlayer().getName() + "'s choices");
        }
    }

    public void setup(String message){
        if (gameServer.isChallenger()) {
            if (GodType.contains(message)) {
                if (game.getNumberOfAvailableGods() == game.getNumberOfActivePlayers()) {
                    gameServer.asyncSend("You already chose the available gods, choose the first player:");
                } else {
                    if (game.availableGodsContains(GodType.parse(message))) {
                        gameServer.asyncSend("You already chose this God, choose another God form the list: ");
                    } else {
                        controller.addAvailableGods(GodType.parse(message));
                        if (game.getNumberOfAvailableGods() == game.getNumberOfActivePlayers()) {
                            gameServer.asyncSend("You set the available gods successfully! Now chose the first player:");
                        } else {
                            gameServer.asyncSend("Choose another god: ");
                        }
                    }
                }
            } else if (game.findPlayerByName(message) != null) {
                if (game.getNumberOfAvailableGods() == game.getNumberOfActivePlayers()) {
                    controller.setCurrentPlayer(game.findPlayerByName(message).getIndex());
                    controller.chooseGods();
                    gameServer.asyncSend("You set the first player correctly, now the game will start!");
                    AvailableGodsChoice availableGodsChoice = new AvailableGodsChoice(game.getAvailableGods());
                    System.out.println("availableGodsChoice created");
                    server.setupEnd(availableGodsChoice);
                } else { //the challenger has not choose all the gods yet
                    gameServer.asyncSend("This god doesn't exist! Choose another god from the list: ");
                }
            } else {
                gameServer.asyncSend("ERROR!");
            }
        } else {
            gameServer.asyncSend("You are NOT the challenger");
        }
    }

    private void chooseGods(String message){
        if(gameServer.isCurrentPlayer()){
            gameServer.asyncSend("You are the current Player!");
            gameServer.asyncSend("Echo: " + message);
        }
        else{
            gameServer.asyncSend("You are NOT the current Player!");
        }
    }


}

package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.gods.GodType;

import java.util.ArrayList;
import java.util.List;

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
        }
        else if(game.getStatus().equals(GameStatus.PLACING)){
            placingWorkers(message);
        }
        else if(game.getStatus().equals(GameStatus.PLAYING)){
            playing(message);
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

    public void placingMessage(WorldDisplay display){
        gameServer.asyncSend(display);
        if(gameServer.isCurrentPlayer()){
            gameServer.asyncSend("You are the current Player choose your where to place your workers type \"place x,y\": ");
        }
        else{
            gameServer.asyncSend("You are NOT the current Player, wait for " + game.getCurrentPlayer().getName() + "'s choices");
        }
    }

    public void updateWorldMessage(WorldDisplay display){
        System.out.println(gameServer.getPlayer().getName() + " updateWorldMessage");
        gameServer.asyncSend(display);
        if(!gameServer.isCurrentPlayer()){
            gameServer.asyncSend(this.game.getCurrentPlayer().getName() + " did a move!");
        }
    }

    public void startTurnMessage(AvailableWorkersDisplay display){
        if(gameServer.isCurrentPlayer()){
            gameServer.asyncSend("It's your turn!");
            gameServer.asyncSend(display);
        }
        else{
            gameServer.asyncSend("You are NOT the current Player, wait for " + game.getCurrentPlayer().getName() + "'s choices");
        }
    }

    public void playGameMessage(WorldDisplay display){

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
                    server.sendChooseGodsMessage(availableGodsChoice);
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
            if(GodType.contains(message)){ //Checks if the message is a god
                if(game.availableGodsContains(GodType.parse(message))){ //Checks if the message is available god
                    controller.setPlayerGod(this.gameServer.getPlayer(), GodType.parse(message)); //sets the god
                    controller.removeAvailableGod(GodType.parse(message));
                    gameServer.asyncSend("Your operation was successful, " + message.toUpperCase() + " is now your God!");
                    controller.nextTurn();
                    if(game.getCurrentPlayer().getGod() != null){
                        controller.placeWorkers();
                        server.sendPlacingMessage(this.game);
                    }
                    else{
                        AvailableGodsChoice availableGodsChoice = new AvailableGodsChoice(game.getAvailableGods());
                        server.sendChooseGodsMessage(availableGodsChoice);
                    }
                }
                else{
                    gameServer.asyncSend(message.toUpperCase() + " is not available. Choose again:");
                }
            }
            else{
                gameServer.asyncSend(message.toUpperCase() + " is not a God. Choose again:");
            }

        }
        else{
            gameServer.asyncSend("You are NOT the current Player!");
        }
    }

    private void placingWorkers(String message){
        if(gameServer.isCurrentPlayer()){
            if(message.startsWith("place")){
                String[] splitMessage = message.split(" ");
                if(splitMessage.length == 2 && splitMessage[1].contains(",") && splitMessage[1].length() == 3){
                    splitMessage = splitMessage[1].split(",");
                    if(isNumber(splitMessage[0]) && isNumber(splitMessage[1])){
                        int x = Integer.parseInt(splitMessage[0]);
                        int y = Integer.parseInt(splitMessage[1]);
                        if(this.game.getWorld().isInWorld(x, y)){
                            if(game.getCurrentPlayer().getAllWorkers().get(0).getCurrentSpace() == null){
                                try{
                                    controller.place(this.game.getCurrentPlayer().getAllWorkers().get(0), this.game.getWorld().getSpaces(x, y));
                                    gameServer.asyncSend("You set your worker number 1 successfully on the space [" + x + "][" + y + "]!");
                                    server.sendUpdateWorldMessage(this.game);
                                    gameServer.asyncSend("Now place the other worker, type \"place x,y\":");
                                }catch (NotExecutedException e){
                                    gameServer.asyncSend(e.getMessage());
                                }
                            }
                            else if(game.getCurrentPlayer().getAllWorkers().get(1).getCurrentSpace() == null){
                                try{
                                    controller.place(this.game.getCurrentPlayer().getAllWorkers().get(1), this.game.getWorld().getSpaces(x, y));
                                    gameServer.asyncSend("You set your worker number 2 successfully on the space [" + x + "][" + y + "]!");
                                    server.sendUpdateWorldMessage(this.game); //TODO: does not show to the current player
                                    gameServer.asyncSend("You set all your workers successfully!");
                                    controller.nextTurn();
                                    if(game.getCurrentPlayer().getAllWorkers().get(0).getCurrentSpace() != null && game.getCurrentPlayer().getAllWorkers().get(1).getCurrentSpace() != null){
                                        controller.playGame();
                                        server.sendUpdateWorldMessage(this.game);
                                        List<Integer> n = new ArrayList<>();
                                        game.getCurrentPlayer().getAvailableWorkers().forEach(w -> n.add(w.getIndex()));
                                        AvailableWorkersDisplay availableWorkers = new AvailableWorkersDisplay(n);
                                        server.sendStartTurnMessage(availableWorkers);
                                    }
                                    else{
                                        server.sendPlacingMessage(this.game);
                                    }
                                }catch (NotExecutedException e){
                                    gameServer.asyncSend(e.getMessage());
                                }
                            }
                            else{
                                gameServer.asyncSend("You already placed all your workers!");
                            }
                        }
                        else{
                            gameServer.asyncSend("ERROR Space out of the borders!");
                        }
                    }
                    else{
                        gameServer.asyncSend("ERROR not number should be \"place x,y\"!");
                    }
                }
                else{
                    gameServer.asyncSend("ERROR wrong format should be \"place x,y\"!");
                }
            }
            else{
                gameServer.asyncSend("ERROR wrong format should be \"place x,y\"!");
            }

        }
        else{
            gameServer.asyncSend("You are NOT the current Player!");
        }
    }

    private void playing(String message){
        if(gameServer.isCurrentPlayer()){
            gameServer.asyncSend("Echo: " + message);
        }
        else {
            gameServer.asyncSend("You are NOT the current Player!");
        }
    }

    private boolean isNumber(String s){
        if(s == null){
            return false;
        }
        try{
            Integer.parseInt(s);
            return true;
        }catch(NumberFormatException nfe){
            return false;
        }
    }

}

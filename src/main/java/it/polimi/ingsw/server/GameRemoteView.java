package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.views.utils.Coordinates;
import it.polimi.ingsw.views.utils.Patterns;

import java.util.*;
import java.util.regex.Pattern;

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

    public void playGameMessage(ActionDisplay display){
        if(gameServer.isCurrentPlayer()){
            gameServer.asyncSend(display);
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
                    server.sendChooseGodsMessage(this.game);
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
                        server.sendChooseGodsMessage(this.game);
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
            if(Pattern.matches(Patterns.placePattern, message)){
                int x = Integer.parseInt(message.substring(6,7));
                int y = Integer.parseInt(message.substring(8,9));
                System.out.print(x + ", " + y);
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
                                server.sendStartTurnMessage(this.game);
                                //TODO: check defeat (is possible to lose right after the placing phase!)
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
            }
            else{
                gameServer.asyncSend("ERROR! Wrong format should be \"place x,y\"!");
            }

        }
        else{
            gameServer.asyncSend("You are NOT the current Player!");
        }
    }

    private void playing(String message){
        if(gameServer.isCurrentPlayer()) {
            if (!game.getCurrentPlayer().hasSelectedAWorker()) {
                if (Pattern.matches(Patterns.workerSelectionPattern, message)) {
                    int index = Integer.parseInt(message.substring(7, 8));
                    if(index == 0 || index == 1){
                        controller.selectWorker(index);
                        gameServer.asyncSend("You selected worker number " + this.game.getCurrentPlayer().getSelectedWorker().getIndex());
                        gameServer.asyncSend("Phase: " + this.game.getTurnPhase());
                        Map<WorkerActionType, List<Coordinates>> actions = workerActionTypeListMap(this.game.getTurnPhase());
                        ActionDisplay display = new ActionDisplay(actions);
                        this.playGameMessage(display);
                    }
                    else{
                        gameServer.asyncSend("ERROR! Worker number " + index + " doesn't exist!");
                    }
                } else {
                    gameServer.asyncSend("ERROR! Wrong format should be \"select number\"!");
                }
            }
            else{ //current player has already selected a worker
                Worker selectedWorker = this.game.getCurrentPlayer().getSelectedWorker();
                int phase = this.game.getTurnPhase();
                List<WorkerActionType> possibleActions = this.game.getRules().possibleActions(phase, selectedWorker);
                if(Pattern.matches(Patterns.movePattern, message)){
                    if(possibleActions.contains(WorkerActionType.MOVE)){
                        int x = Integer.parseInt(message.substring(5, 6));
                        int y = Integer.parseInt(message.substring(7, 8));
                        Space targetSpace = this.game.getWorld().getSpaces(x, y);
                        try{
                            controller.move(selectedWorker, targetSpace);
                            gameServer.asyncSend("You moved your worker to [" + selectedWorker.getCurrentSpace().getX() + "][" + selectedWorker.getCurrentSpace().getY() + "]");
                            server.sendUpdateWorldMessage(this.game);
                            gameServer.asyncSend("Phase: " + this.game.getTurnPhase());
                            Map<WorkerActionType, List<Coordinates>> actions = workerActionTypeListMap(this.game.getTurnPhase());
                            ActionDisplay display = new ActionDisplay(actions);
                            this.playGameMessage(display);
                        }
                        catch (NotExecutedException e){
                            gameServer.asyncSend(e.getMessage());
                        }
                    }
                    else{
                        gameServer.asyncSend("You cannot move!");
                        Map<WorkerActionType, List<Coordinates>> actions = workerActionTypeListMap(this.game.getTurnPhase());
                        ActionDisplay display = new ActionDisplay(actions);
                        this.playGameMessage(display);
                    }
                }
                else if(Pattern.matches(Patterns.buildPattern, message)){
                    if(possibleActions.contains(WorkerActionType.BUILD)) {
                        int x = Integer.parseInt(message.substring(6, 7));
                        int y = Integer.parseInt(message.substring(8, 9));
                        Space targetSpace = this.game.getWorld().getSpaces(x, y);
                        try{
                            controller.build(selectedWorker, targetSpace);
                            gameServer.asyncSend("Your worker built in [" + selectedWorker.previousBuild().getX() + "][" + selectedWorker.previousBuild().getY() + "]");
                            server.sendUpdateWorldMessage(this.game);
                            gameServer.asyncSend("Phase: " + this.game.getTurnPhase());
                            Map<WorkerActionType, List<Coordinates>> actions = workerActionTypeListMap(this.game.getTurnPhase());
                            ActionDisplay display = new ActionDisplay(actions);
                            this.playGameMessage(display);
                        }
                        catch (NotExecutedException e){
                            gameServer.asyncSend(e.getMessage());
                        }
                    }
                    else{
                        gameServer.asyncSend("You cannot build!");
                        Map<WorkerActionType, List<Coordinates>> actions = workerActionTypeListMap(this.game.getTurnPhase());
                        ActionDisplay display = new ActionDisplay(actions);
                        this.playGameMessage(display);
                    }
                }
                else if(Pattern.matches(Patterns.buildDomePattern, message)){
                    if(possibleActions.contains(WorkerActionType.BUILD_DOME)) {
                        int x = Integer.parseInt(message.substring(5, 6));
                        int y = Integer.parseInt(message.substring(7, 8));
                        Space targetSpace = this.game.getWorld().getSpaces(x, y);
                        try{
                            controller.buildDome(selectedWorker, targetSpace);
                            gameServer.asyncSend("Your worker built a dome in [" + selectedWorker.previousDome().getX() + "][" + selectedWorker.previousDome().getY() + "]");
                            server.sendUpdateWorldMessage(this.game);
                            gameServer.asyncSend("Phase: " + this.game.getTurnPhase());
                            Map<WorkerActionType, List<Coordinates>> actions = workerActionTypeListMap(this.game.getTurnPhase());
                            ActionDisplay display = new ActionDisplay(actions);
                            this.playGameMessage(display);
                        }
                        catch (NotExecutedException e){
                            gameServer.asyncSend(e.getMessage());
                        }
                    }
                    else{
                        gameServer.asyncSend("You cannot build a dome!");
                        Map<WorkerActionType, List<Coordinates>> actions = workerActionTypeListMap(this.game.getTurnPhase());
                        ActionDisplay display = new ActionDisplay(actions);
                        this.playGameMessage(display);
                    }
                }
                else if(message.equals("end")){
                    if(possibleActions.contains(WorkerActionType.END_TURN)) {
                        gameServer.asyncSend("You ended your turn!");
                        controller.resetTurn();
                        controller.nextTurn();
                        server.sendStartTurnMessage(this.game);
                    }
                    else{
                        gameServer.asyncSend("You cannot end your turn yet!");
                        Map<WorkerActionType, List<Coordinates>> actions = workerActionTypeListMap(this.game.getTurnPhase());
                        ActionDisplay display = new ActionDisplay(actions);
                        this.playGameMessage(display);
                    }
                }
                else{
                    gameServer.asyncSend("Your command doesn't exist!");
                    Map<WorkerActionType, List<Coordinates>> actions = workerActionTypeListMap(this.game.getTurnPhase());
                    ActionDisplay display = new ActionDisplay(actions);
                    this.playGameMessage(display);
                }
            }
        }
        else {
            gameServer.asyncSend("You are NOT the current Player!");
        }
    }

    private Map<WorkerActionType, List<Coordinates>> workerActionTypeListMap(int phase){
        Map<WorkerActionType, List<Coordinates>> actions = new HashMap<>();
        Worker selectedWorker = this.game.getCurrentPlayer().getSelectedWorker();
        List<WorkerActionType> possibleActions = this.game.getRules().possibleActions(phase, selectedWorker);
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
        return actions;
    }

}

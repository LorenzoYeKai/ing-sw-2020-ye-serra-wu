package it.polimi.ingsw.server;

import it.polimi.ingsw.client.ChooseActionTurnMessage;
import it.polimi.ingsw.client.GameSetupReply;
import it.polimi.ingsw.client.TurnMessageReplay;
import it.polimi.ingsw.client.WorkerStartPosition;
import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.WorkerData;
import it.polimi.ingsw.models.game.gods.GodType;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class GameServer implements Runnable{

    private Socket socket;
    private GameController controller;
    private Game game;


    public GameServer(GameController controller,Socket socket,Game game) throws NotExecutedException {
        this.socket= socket;
        this.controller= controller;
        this.game= game;
    }



    @Override
    public void run() {
        try {
            GameSetup gameSetup = new GameSetup(GodType.getListOfGods(), game.getWorld(), game.getListOfPlayers());
            ByteArrayOutputStream gameSetupArrayOut= new ByteArrayOutputStream();
            ObjectOutputStream gameSetupOut = new ObjectOutputStream(gameSetupArrayOut);
            clientUpdate(gameSetupOut, gameSetupArrayOut, gameSetup);  //sappiamo anche il challenger
            ByteArrayInputStream gameSetupArrayIn = new ByteArrayInputStream(gameSetupArrayOut.toByteArray());
            ObjectInputStream gameSetupIn = new ObjectInputStream(gameSetupArrayIn);
            GameSetupReply gameSetupReply = (GameSetupReply) gameSetupIn.readObject();
            if(game.getListOfPlayers().get(0).getName().equals(gameSetupReply.getName())){
                controller.setAvailableGods(gameSetupReply.getAvailableGods());
                controller.setCurrentPlayer(gameSetupReply.getPlayerIndex());
            }

            // fase scelta giocatori da fare

            for(int x=0; x < game.getNumberOfActivePlayers(); x++){   // scelta del potere
                WorkerStartPosition workerStartPositionReply = (WorkerStartPosition) gameSetupIn.readObject();
                if(game.getCurrentPlayer().getName().equals(workerStartPositionReply.getName()))
                {
                    controller.place(game.getCurrentPlayer().getAllWorkers().get(0),
                            game.getWorld().getSpaces(workerStartPositionReply.getX(0), workerStartPositionReply.getY(0)));
                    controller.place(game.getCurrentPlayer().getAllWorkers().get(1),
                            game.getWorld().getSpaces(workerStartPositionReply.getX(1), workerStartPositionReply.getY(1)));

                    controller.nextTurn();
                }
                WorldDisplay worldDisplay= new WorldDisplay(game);  // aggiorna il tabellone ogni volta
                clientUpdate(gameSetupOut, gameSetupArrayOut, worldDisplay);
            }
            while(true)
            {
                TurnMessage turnMessage = new TurnMessage();  //  scelta del worker
                clientUpdate(gameSetupOut, gameSetupArrayOut, turnMessage);
                TurnMessageReplay turnMessageReplay = (TurnMessageReplay) gameSetupIn.readObject();
                int phase=0;

                while(true){ // cicla fino a quando non premo end turn

                    if(game.getCurrentPlayer().getName().equals(turnMessageReplay.getName())) {
                        ArrayList<Space> availableSpaces = turnMessageReplay.getWorker().computeAvailableSpaces();
                        TurnMessageAvaiableAction turnMessageAvaiableAction = new TurnMessageAvaiableAction();
                        clientUpdate(gameSetupOut, gameSetupArrayOut, turnMessageAvaiableAction);
                        ChooseActionTurnMessage reply = (ChooseActionTurnMessage) gameSetupIn.readObject();
                            if (!reply.getType().equals("EndTurn")) {
                                controller.checkDefeat(reply.getType(),turnMessageReplay.getWorker());
                                if (game.getCurrentPlayer().isDefeated()) {


                                    controller.handleDefeat(game.getCurrentPlayer());
                                    WorldDisplay worldDisplay = new WorldDisplay(game);
                                    clientUpdate(gameSetupOut, gameSetupArrayOut, worldDisplay);
                                    break;
                                }
                                updateWorld(turnMessageReplay.getWorker(), reply.getType(), reply.getX(), reply.getY());
                                WorldDisplay worldDisplay = new WorldDisplay(game);
                                clientUpdate(gameSetupOut, gameSetupArrayOut, worldDisplay);
                                if(game.getListOfPlayers().size()==1)
                                {
                                    VictoryMessage victoryMessage  ;
                                    clientUpdate(gameSetupOut, gameSetupArrayOut, turnMessageAvaiableAction);
                                    // chiudo le connessioni
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    phase++;
                }
        } catch (IOException | ClassNotFoundException | NotExecutedException e){
            System.err.println(e.getMessage());
        }



    }

    private void clientUpdate(ObjectOutputStream outObject,ByteArrayOutputStream outByteArray,Object objectInput) throws IOException {
        outObject.reset();
        outObject.writeObject(objectInput);
        outObject.flush();

    }
    public void updateWorld (WorkerData worker, WorkerActionType typeAction, int x, int y) throws NotExecutedException {
        Space targetSpace = game.getWorld().getSpaces(x , y);
        Space currentSpace = worker.getCurrentSpace();
        controller.workerAction( worker , typeAction ,targetSpace.getX(),targetSpace.getY() );
    }


}

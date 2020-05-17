package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.gods.GodType;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameServer implements Runnable{

    private Socket socket;
    private GameController controller;
    private Game game;


    public GameServer(GameController controller,Socket socket,Game game)
    {
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
            GameSetupReply reply = (GameSetupReply) gameSetupIn.readObject();
            if(game.getListOfPlayers().get(0).getName().equals(reply.getName())){
                controller.setAvailableGods(reply.getAvailableGods());
                controller.setCurrentPlayer(replay.getCurrentPlayer());
            }
            for(int x=0; x < game.getNumberOfActivePlayers(); x++){
                WorkerStartPosition reply = (WorkerStartPosition) gameSetupIn.readObject();
                if(game.getCurrentPlayer().getName().equals(reply.getName()))
                {
                    controller.place(game.getCurrentPlayer().getAllWorkers().get(0),
                            game.getWorld().getSpaces(reply.getX(0), reply.getY(0)));
                    controller.nextTurn();
                }
                WorldDisplay worldDisplay= new WorldDisplay(game);  // aggiorna il tabellone ogni volta
                clientUpdate(gameSetupOut, gameSetupArrayOut, worldDisplay);
            }
            while(true)
            {
                TurnMessage turnMessage = new TurnMessage(game.getCurrentPlayer());  //  scelta del worker
                clientUpdate(gameSetupOut, gameSetupArrayOut, turnMessage);
                TurnMessageReplay turnMessageReplay = (TurnMessageReplay) gameSetupIn.readObject();
                while(true){ // cicla fino a quando non premo end turn
                    if(game.getCurrentPlayer().getName().equals(turnMessageReply.getName())) {
                        TurnMessageAvaiableAction turnMessageAvaiableAction = new TurnMessageAvaiableAction(game.getCurrentPlayer(),game.getCurrentPlayer().getGod().getAvaiableAction());
                        clientUpdate(gameSetupOut, gameSetupArrayOut, turnMessageAvaiableAction);
                        ChooseActionTurnMessage chooseActionTurnMessage = new ChooseActionTurnMessage(game.getCurrentPlayer());
                        if (chooseActionTurnMessage.getType != "EndTurn") {
                            updateWorld(turnMessageReplay.getWorker, chooseActionTurnMessage.getType, chooseActionTurnMessage.getX, chooseActionTurnMessage.gety); // controllo nel caso di vittoria o sconfitta
                            WorldDisplay worldDisplay= new WorldDisplay(game);
                            clientUpdate(gameSetupOut, gameSetupArrayOut, worldDisplay);
                        }else{
                            break;
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException  e){
            System.err.println(e.getMessage());
        }




        try{
            Scanner inString = new Scanner(socket.getInputStream());
            PrintWriter outString = new PrintWriter(socket.getOutputStream());
            ByteArrayOutputStream outByteArray = new ByteArrayOutputStream();
            ObjectOutputStream outObject  = new ObjectOutputStream (outByteArray);




            while(true)
            {

            }
        }

    }

    private void clientUpdate(ObjectOutputStream outObject,ByteArrayOutputStream outByteArray,Object objectInput) throws IOException {
        outObject.reset();
        outObject.writeObject(objectInput);
        outObject.flush();

    }
    public void updateWorld (Worker worker, WorkerActionType typeAction, int x, int y);
    {
        Space currentSpace = new Space (game.getCurrentPlayer().getWorker().getX,game.getCurrentPlayer().getWorker().getY);
        Space targetSpace = new Space (game.getWorld().getSpaces().getX(),game.getWorld().getSpaces().getY());
        if (game.getRules().winCondition(currentSpace,targetSpace)){
            game.announceVictory();
            break;
        }
        controller.workerAction( worker, typeAction ,targetSpace.getX(),targetSpace,getY );
    }

}

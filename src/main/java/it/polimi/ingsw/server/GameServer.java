package it.polimi.ingsw.server;

import it.polimi.ingsw.Notifier;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameServer implements Runnable{ //connection for the game

    private Socket socket;

    private GameController controller;
    private Game game;
    private int firstPlayerIndex;
    private String name;
    private Notifier<String> onStringReceived;
    private Notifier<GameSetup> setupInfoNotifier;
    private Notifier<AvailableGodsChoice> chooseGodsNotifier;
    private GameRemoteView remoteView;
    private ObjectOutputStream objectOutputStream;

    public GameServer(GameController controller,Socket socket,Game game, String name, Server server) throws NotExecutedException, IOException {
        this.socket= socket;
        this.controller= controller;
        this.game= game;
        this.name = name;
        this.remoteView = new GameRemoteView(this, game, controller, server);
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.onStringReceived = new Notifier<>();
        this.setupInfoNotifier = new Notifier<>();
        this.chooseGodsNotifier = new Notifier<>();
    }

    public synchronized void send(Object message) {
        try {
            objectOutputStream.reset();
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch(IOException e){
            System.err.println(e.getMessage());
        }

    }


    public void asyncSend(final Object message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                send(message);
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Game Started!");
            addListeners();
            //Streams declarations
            Scanner stringInput = new Scanner(socket.getInputStream());
            /*PrintWriter stringOutput = new PrintWriter(socket.getOutputStream());*/
            /*ByteArrayOutputStream gameSetupArrayOut= new ByteArrayOutputStream();*/

            /*ByteArrayInputStream gameSetupArrayIn = new ByteArrayInputStream(gameSetupArrayOut.toByteArray());
            ObjectInputStream gameSetupIn = new ObjectInputStream(gameSetupArrayIn);*/

            send("You entered in a game!");

            GameSetup gameSetup = new GameSetup(GodType.getListOfGods(), game.getWorld(), game.getListOfPlayers());
            setupInfoNotifier.notify(gameSetup);
            String read;

            while (true) {
                read = stringInput.nextLine();
                onStringReceived.notify(read);
            }
            //challengerMessage(stringOutput);
            //stopSending(stringOutput);

            /*if(isChallenger()) {
                while(true){
                    String read = stringInput.nextLine();
                    if(read.equals("end")){
                        break;
                    }
                    onStringReceived.notify(read);
                }
            }
            else{
                while (true){

                }
            }
            stringOutput.println("The game ended!");
            stringOutput.flush();*/
        }catch(IOException  /*| InterruptedException*/ e){
            System.err.println(e.getMessage());
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
            /*GameSetup gameSetup = new GameSetup(game.getListOfPlayers().get(0).getName(), GodType.getListOfGods(), game.getWorld(), game.getListOfPlayers());

            ByteArrayOutputStream gameSetupArrayOut= new ByteArrayOutputStream();
            ObjectOutputStream gameSetupOut = new ObjectOutputStream(gameSetupArrayOut);
            ByteArrayInputStream gameSetupArrayIn = new ByteArrayInputStream(gameSetupArrayOut.toByteArray());
            ObjectInputStream gameSetupIn = new ObjectInputStream(gameSetupArrayIn);

            clientUpdate(gameSetupOut, gameSetupArrayOut, gameSetup); //Sends the information for the setUp phase
            setupPhase(gameSetupIn); //Receives the available gods and the first player from the challenger




            do {
                WorkerSetupReply workerStartPositionReply = (WorkerSetupReply) gameSetupIn.readObject();

                if (game.getCurrentPlayer().getName().equals(workerStartPositionReply.getClientName())) {
                    controller.place(game.getCurrentPlayer().getAllWorkers().get(0),
                            game.getWorld().getSpaces(workerStartPositionReply.getX(0), workerStartPositionReply.getY(0)));
                    controller.place(game.getCurrentPlayer().getAllWorkers().get(1),
                            game.getWorld().getSpaces(workerStartPositionReply.getX(1), workerStartPositionReply.getY(1)));
                    controller.nextTurn();
                    WorldDisplay worldDisplay = new WorldDisplay(game);
                    clientUpdate(gameSetupOut, gameSetupArrayOut, worldDisplay);
                }
            } while (game.getCurrentPlayer().getIndex() != this.firstPlayerIndex);

            while(true)
            {
                WorkerChoice turnMessage = new WorkerChoice();  //  scelta del worker
                clientUpdate(gameSetupOut, gameSetupArrayOut, turnMessage);
                AvailableActionsReply turnMessageReplay = (AvailableActionsReply) gameSetupIn.readObject();
                int phase=0;

                while(true){ // cicla fino a quando non premo end turn

                    if(game.getCurrentPlayer().getName().equals(turnMessageReplay.getName())) {
                        ArrayList<Space> availableSpaces = turnMessageReplay.getWorker().computeAvailableSpaces();
                        AvailableActions turnMessageAvaiableAction = new AvailableActions();
                        clientUpdate(gameSetupOut, gameSetupArrayOut, turnMessageAvaiableAction);
                        ActionMessage reply = (ActionMessage) gameSetupIn.readObject();
                            if (!reply.getType().equals(WorkerActionType.END_TURN)) {
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
        }*/



    }


    /*private void clientUpdate(ObjectOutputStream outObject,ByteArrayOutputStream outByteArray,Object objectInput) throws IOException {
        outObject.reset();
        outObject.writeObject(objectInput);
        outObject.flush();
    }

    public void updateWorld (WorkerData worker, WorkerActionType typeAction, int x, int y) throws NotExecutedException {
        Space targetSpace = game.getWorld().getSpaces(x , y);
        Space currentSpace = worker.getCurrentSpace();
        controller.workerAction( worker , typeAction ,targetSpace.getX(),targetSpace.getY() );
    }*/

    /*private synchronized void setup(Scanner stringInput, String read) throws IOException, ClassNotFoundException {
        GameSetup gameSetup = new GameSetup(GodType.getListOfGods(), game.getWorld(), game.getListOfPlayers());
        setupInfoNotifier.notify(gameSetup);

        while (true) {
            System.out.println("SETUP");
            read = stringInput.nextLine();
            onStringReceived.notify(read);
        }
    }*/

    public GameRemoteView getRemoteView(){
        return this.remoteView;
    }

    private void addListeners(){
        onStringReceived.addListener(remoteView, message -> remoteView.clientMessageHandler(message));
        setupInfoNotifier.addListener(remoteView, message -> remoteView.setupMessage(message));
        chooseGodsNotifier.addListener(remoteView, message -> remoteView.chooseGodsMessage(message));
    }


    public boolean isChallenger(){
        return this.name.equals(game.getListOfPlayers().get(0).getName());
    }

    public boolean isCurrentPlayer(){
        return this.name.equals(game.getCurrentPlayer().getName());
    }

    public Player getPlayer(){
        return game.findPlayerByName(this.name);
    }

}

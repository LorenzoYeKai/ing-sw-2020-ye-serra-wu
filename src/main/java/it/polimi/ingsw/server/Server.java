package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT= 12345;
    private ServerSocket serverSocket;
    private ExecutorService executor = Executors.newFixedThreadPool(128);
    private List<String> nicknames = new ArrayList<>();
    private Map<String, Socket> lobbyWaitingList = new HashMap<>();
    private Set<GameServer> gameServers;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
        this.gameServers = new HashSet<>();
    }

    public void run(){
        System.out.println("Server listening on port: " + PORT);
        while(true){
            try {
                Socket socket = serverSocket.accept();
                TempLobby lobby = new TempLobby(socket, this);
                executor.submit(lobby);
            } catch (IOException e){
                System.err.println("Connection error!");
            }
        }
    }

    public void lobby(Socket socket, String nickname) throws NotExecutedException, IOException {
        this.lobbyWaitingList.put(nickname, socket);
        this.nicknames.add(nickname);
        if(this.lobbyWaitingList.size() == 3){
            System.out.println("We are ready for a Game!\nPlayers online:");
            this.nicknames.forEach(System.out::println);
            List<Socket> values = new ArrayList<>(lobbyWaitingList.values());
            List<String> keys = new ArrayList<>(lobbyWaitingList.keySet());
            GameController gameController = new GameController(this.nicknames);
            gameController.setupGame();
            GameServer gameServer1 = new GameServer(gameController, values.get(0), gameController.getGame(), keys.get(0), this);
            GameServer gameServer2 = new GameServer(gameController, values.get(1), gameController.getGame(), keys.get(1), this);
            GameServer gameServer3 = new GameServer(gameController, values.get(2), gameController.getGame(), keys.get(2), this);
            this.gameServers.add(gameServer1);
            this.gameServers.add(gameServer2);
            this.gameServers.add(gameServer3);
            executor.submit(gameServer1);
            executor.submit(gameServer2);
            executor.submit(gameServer3);
        }
    }

    public void sendChooseGodsMessage(Game game){
        AvailableGodsChoice availableGodsChoice = new AvailableGodsChoice(game.getAvailableGods());
        this.gameServers.forEach(g -> g.getRemoteView().chooseGodsMessage(availableGodsChoice));
    }

    public void sendPlacingMessage(Game game){
        WorldDisplay display = new WorldDisplay(game);
        this.gameServers.forEach(g -> g.getRemoteView().placingMessage(display));
    }

    public void sendUpdateWorldMessage(Game game){
        WorldDisplay display = new WorldDisplay(game);
        this.gameServers.forEach(g -> g.getRemoteView().updateWorldMessage(display));
    }

    public void sendStartTurnMessage(){
        AvailableWorkersDisplay display = new AvailableWorkersDisplay();
        this.gameServers.forEach(g -> g.getRemoteView().startTurnMessage(display));
    }

    public List<String> getNicknames(){
        return this.nicknames;
    }

    public void addNicknames(String n){
        this.nicknames.add(n);
    }
}

package it.polimi.ingsw.server;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.controller.lobby.LocalLobbyController;
import it.polimi.ingsw.controller.lobby.remote.ServerLobbyController;
import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.requests.RequestProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 12345;
    private final ServerSocket serverSocket;
    private final LobbyController lobby = new LocalLobbyController();
    private final ExecutorService executor = Executors.newFixedThreadPool(128);

    //private Set<GameServer> gameServers;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
        //this.gameServers = new HashSet<>();
    }

    public void run() {
        System.out.println("Server listening on port: " + PORT);
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(() -> this.handleClientConnection(socket));
            } catch (IOException e) {
                System.err.println("Connection error!");
            }
        }
    }

    private void handleClientConnection(Socket newSocket) {
        try (RequestProcessor processor = new RequestProcessor(newSocket);
             ServerLobbyController lobby = new ServerLobbyController(processor, this.lobby)) {
            processor.invokeAsync(() -> processor.addHandler(lobby));
            processor.runEventLoop();
        } catch (Exception e) {
            System.err.println("Client connection terminated");
            e.printStackTrace();
        }
    }


}

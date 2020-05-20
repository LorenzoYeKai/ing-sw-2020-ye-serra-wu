package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {

    private String ip;
    private int port;

    public Client(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void run() throws IOException {
        Socket socket = new Socket(ip, port);
        System.out.println("Connection established");
        TempLobbyClient lobbyClient = new TempLobbyClient(socket, this);
        lobbyClient.run();
}
}

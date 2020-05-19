package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TempLobbyClient {

    private Socket socket;
    private Client client;

    public TempLobbyClient(Socket socket, Client client){
        this.socket = socket;
        this.client = client;
    }

    public void run() throws IOException {
        Scanner socketIn = new Scanner(socket.getInputStream());
        PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
        Scanner stdin = new Scanner(System.in);
        String socketLine;
        try {
            socketLine = socketIn.nextLine();
            System.out.println(socketLine);

            String inputLine = stdin.nextLine();
            socketOut.println(inputLine);
            socketOut.flush();
            GameClient gameClient = new GameClient(inputLine, socket);
            gameClient.run();
        } catch(NoSuchElementException e){
            System.out.println("Connection closed from the client side");
        } finally {
            stdin.close();
            socketIn.close();
            socketOut.close();
        }
    }
}

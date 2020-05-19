package it.polimi.ingsw.server;

import it.polimi.ingsw.Notifiable;
import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.Game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TempLobby implements Runnable { //Connection for the loby

    private Socket socket;
    private Server server;
    private Notifiable<String> onNicknameReceived;

    public TempLobby(Socket socket, Server server){
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            Scanner nicknameReceiver = new Scanner(socket.getInputStream());
            PrintWriter messageSender = new PrintWriter(socket.getOutputStream());
            messageSender.println("You are in the lobby. Please, enter a nickname:");
            messageSender.flush();
            stopSending(messageSender);
            String socketInput = nicknameReceiver.nextLine();
            server.lobby(this.socket, socketInput);
        } catch(IOException | NotExecutedException e){
            System.err.println(e.getMessage());
        }
    }

    private void stopSending(PrintWriter stringOutput){
        stringOutput.println("end");
        stringOutput.flush();
    }
}

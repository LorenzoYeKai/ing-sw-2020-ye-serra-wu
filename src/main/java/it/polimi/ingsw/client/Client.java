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

        /*Scanner socketIn = new Scanner(socket.getInputStream());
        PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
        Scanner stdin = new Scanner(System.in);
        String socketLine;
        try{
            while(true) {
                socketLine = socketIn.nextLine();
                if(!socketLine.equals("end")) {
                    System.out.println(socketLine);
                }
                else{
                    break;
                }
            }
            while (true){
                while(true) {
                    String inputLine = stdin.nextLine();
                    if(!inputLine.equals("end")) {
                        socketOut.println(inputLine);
                        socketOut.flush();
                    }
                    else{
                        break;
                    }
                }
                while(true) {
                    socketLine = socketIn.nextLine();
                    if (!socketLine.equals("end")) {
                        System.out.println(socketLine);
                    } else {
                        break;
                    }
                }
            }
        } catch(NoSuchElementException e){
            System.out.println("Connection closed from the client side");
        } finally {
            stdin.close();
            socketIn.close();
            socketOut.close();
            socket.close();
        }*/
    }
}

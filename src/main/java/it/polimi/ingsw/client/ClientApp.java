package it.polimi.ingsw.client;

import it.polimi.ingsw.GUI.GUIApp;
import it.polimi.ingsw.NotExecutedException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ClientApp {

    public static void main(String[] args) {
        if(List.of(args).contains("gui")) {
            GUIApp.main(args);
            return;
        }

        Scanner input = new Scanner(System.in);
        String defaultIP = "127.0.0.1";
        System.out.println("Type Server IP (defaults to " + defaultIP + ")");
        String ip = input.nextLine().strip();
        if (ip.length() == 0) {
            ip = defaultIP;
        }
        System.out.println("Will connect to " + ip);
        try (Client client = new Client(ip, 12345)) {
            client.run();
        } catch (IOException | NotExecutedException e) {
            e.printStackTrace();
        }
    }
}

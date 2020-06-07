package it.polimi.ingsw.client;

import java.io.IOException;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        String defaultIP = "127.0.0.1";
        System.out.println("Type Server IP (defaults to " + defaultIP + ")");
        String ip = input.nextLine().strip();
        if(ip.length() == 0) {
            ip = defaultIP;
        }
        System.out.println("Will connect to " + ip);
        Client client = new Client(ip, 12345);
        try{
            client.run();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}

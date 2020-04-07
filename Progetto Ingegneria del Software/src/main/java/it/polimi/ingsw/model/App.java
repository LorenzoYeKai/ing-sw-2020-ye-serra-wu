package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.views.game.MultiUserConsoleGameView;
import it.polimi.ingsw.views.lobby.MultiUserConsoleLobbyView;

import java.util.Scanner;

/**
 *
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            System.out.println("Creating lobby controller and lobby view...");
            LobbyController lobbyController = new LobbyController();
            MultiUserConsoleLobbyView lobbyView = new MultiUserConsoleLobbyView(lobbyController);
            GameController gameController = lobbyView.getUserInputUntiGameStarts();
            MultiUserConsoleGameView gameView = new MultiUserConsoleGameView(gameController);
            // game created...
            System.out.println("Game should be created now (actually not implemented yet)");
            System.in.read();
        }
        catch (Exception exception) {
            System.out.println("Error: " + exception);
        }
    }
}

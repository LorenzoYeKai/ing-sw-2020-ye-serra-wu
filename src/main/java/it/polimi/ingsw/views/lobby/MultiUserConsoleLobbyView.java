package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.views.game.MultiUserConsoleGameView;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;

/**
 * This is a console-based View of lobby,
 * for multiple lobbyUsers (so multiple lobbyUsers will have the same View),
 * which will use the view by turn.
 */
public class MultiUserConsoleLobbyView {
    private final LobbyController controller;
    private final Scanner input;
    private final PrintStream output;
    private final List<ConsoleLobbyView> views = new ArrayList<>();
    private final List<String> readyUsers = new ArrayList<>();
    private GameController gameController;

    public MultiUserConsoleLobbyView(LobbyController controller)
            throws NotExecutedException, IOException {
        this.controller = controller;
        this.input = new Scanner(System.in);
        this.output = System.out;
        this.output.println("Type your names, end with an empty line.");

        String username = this.input.nextLine();
        // Read first userName
        while (username.isEmpty()) {
            this.output.println("Please type at least one userName");
            username = this.input.nextLine();
        }
        Consumer<GameController> onGameStarted =
                gameController -> this.gameController = gameController;
        this.views.add(new ConsoleLobbyView(username,
                this.controller, this.output, this.getGameStartHandler(username)));

        this.output.println("Type the name of 2nd player, or end with an empty line.");
        // Read more usernames
        username = this.input.nextLine();
        while (!username.isEmpty()) {
            this.views.add(new ConsoleLobbyView(username,
                    this.controller, this.output, this.getGameStartHandler(username)));
            this.output.println("Type the name of more players, or end with an empty line.");
            username = this.input.nextLine();
        }
    }

    public MultiUserConsoleGameView getUserInputUntilGameStarts() {
        while (this.gameController == null) {
            for (ConsoleLobbyView view : this.views) {
                this.getUserInput(view);
                if (this.gameController != null) {
                    break;
                }
            }
        }

        MultiUserConsoleGameView gameView = new MultiUserConsoleGameView(this.gameController);
        for(String name : this.readyUsers) {
            gameView.join(name);
        }
        return gameView;
    }

    private Consumer<GameController> getGameStartHandler(String userName) {
        return gameController -> {
            if(this.gameController == null) {
                this.gameController = gameController;
            }
            if(this.gameController == gameController) {
                this.readyUsers.add(userName);
            }
        };
    }

    private void getUserInput(ConsoleLobbyView view) {
        while(this.gameController == null) {
            this.output.println("Now it's turn of " + view.getUserName());
            this.output.println("Summary: ");
            view.displaySummary();
            this.output.println();
            view.displayInputHint();
            this.output.println("Press ENTER to end your turn.");
            String line = this.input.nextLine();
            if (line.isEmpty()) {
                return;
            }

            String[] splitted = line.split("\\s+", 2);
            if (splitted.length != 2) {
                this.output.println("Wrong input, try again");
                continue;
            }
            String command = splitted[0].toLowerCase();
            String data = splitted[1];

            try {
                view.executeInput(command, data);
            } catch (NotExecutedException exception) {
                this.output.println("Operation rejected: " + exception.getMessage());
            } catch (IOException e) {
                this.output.println("IOException: " + e);
            }
        }
    }
}

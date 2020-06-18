package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.views.game.MultiUserConsoleGameView;
import it.polimi.ingsw.views.utils.ConsoleMatrix;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * This is a console-based View of lobby,
 * for multiple lobbyUsers (so multiple lobbyUsers will have the same View),
 * which will use the view by turn.
 */
public class MultiUserConsoleLobbyView {
    private final LobbyController controller;
    private final Scanner input;
    private final PrintStream output;
    private final List<SharedConsoleLobbyView> views;
    private GameController gameController;

    public MultiUserConsoleLobbyView(LobbyController controller)
            throws NotExecutedException, IOException {
        this.controller = controller;
        this.input = new Scanner(System.in);
        this.output = System.out;
        this.output.println("Type your names, end with an empty line.");
        this.views = new ArrayList<>();

        String username = this.input.nextLine();
        // Read first userName
        while (username.isEmpty()) {
            this.output.println("Please type at least one userName");
            username = this.input.nextLine();
        }
        this.views.add(new SharedConsoleLobbyView(this.controller, username, this.output));

        this.output.println("Type the name of 2nd player, or end with an empty line.");
        // Read more usernames
        username = this.input.nextLine();
        while (!username.isEmpty()) {
            this.views.add(new SharedConsoleLobbyView(this.controller, username, this.output));
            this.output.println("Type the name of more players, or end with an empty line.");
            username = this.input.nextLine();
        }
    }

    public MultiUserConsoleGameView getUserInputUntilGameStarts() {
        while (this.gameController == null) {
            for (SharedConsoleLobbyView view : views) {
                this.getUserInput(view);
                if (this.gameController != null) {
                    break;
                }
            }
        }

        MultiUserConsoleGameView gameView = new MultiUserConsoleGameView(this.gameController);
        for (SharedConsoleLobbyView view : views) {
            if (view.getGameController() == this.gameController) {
                gameView.join(view.getUserName());
            }
        }
        return gameView;
    }

    private void getUserInput(SharedConsoleLobbyView view) {
        while (true) {
            this.output.println("Now it's turn of " + view.getUserName());
            this.output.println("Summary: ");
            view.displaySummary();
            this.output.println();

            if (view.getCurrentRoomName() == null) {
                // if not inside room
                this.output.println("Write `host room` to host room");
                this.output.println("Write `join [Room Name]` to join room");
            } else {
                // if inside room
                this.output.println("Write `leave room` to leave room");
                if (view.getUserName().equals(view.getCurrentRoomName())) {
                    this.output.println("You are host of this room.");
                    this.output.println("Write `up [Name]` to move someone up");
                    this.output.println("Write `down [Name]` to move someone down");
                    this.output.println("Write `kick [Name]` to kick someone");
                    this.output.println("Write `start game` to start the game");
                }
            }
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
            String parameter = splitted[1];

            try {
                switch (command) {
                    case "host" -> this.controller.createRoom(view.getToken());
                    case "join" -> this.controller.joinRoom(view.getToken(), parameter);
                    case "leave" -> this.controller.leaveRoom(view.getToken());
                    case "up" -> this.controller.changePlayerPosition(view.getToken(),
                            parameter,
                            -1);
                    case "down" -> this.controller.changePlayerPosition(view.getToken(),
                            parameter,
                            +1);
                    case "kick" -> this.controller.kickUser(view.getToken(),
                            parameter);
                    case "start" -> this.controller.startGame(view.getToken());
                    default -> this.output.println("Unknown action, skipped");
                }
            } catch (NotExecutedException exception) {
                this.output.println("Operation rejected: " + exception.getMessage());
            } catch (IOException e) {
                this.output.println("IOException: " + e);
            }

            if (view.getGameController() != null) {
                this.output.println("Switching to GameView...");
                this.gameController = view.getGameController();
                return;
            }
        }
    }

}

class SharedConsoleLobbyView implements LobbyView {
    private final PrintStream output;
    private final Set<String> lobbyUsers;
    private final Set<String> lobbyRooms;
    private final String userName;
    private final UserToken token;
    private String lastRoomName;
    private String currentRoomName;
    private final List<String> playersInTheRoom;
    private final List<String> messages;
    private GameController gameController;

    public SharedConsoleLobbyView(LobbyController controller,
                                  String userName,
                                  PrintStream output)
            throws NotExecutedException, IOException {
        this.output = output;
        this.lobbyUsers = new TreeSet<>();
        this.lobbyRooms = new TreeSet<>();
        this.userName = userName;
        this.token = controller.joinLobby(userName, this);
        this.lastRoomName = null;
        this.currentRoomName = null;
        this.playersInTheRoom = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.gameController = null;
    }

    public synchronized String getUserName() {
        return this.userName;
    }

    public synchronized UserToken getToken() {
        return this.token;
    }

    public synchronized String getCurrentRoomName() {
        return this.currentRoomName;
    }

    public synchronized GameController getGameController() {
        return this.gameController;
    }

    public synchronized void displaySummary() {
        ConsoleMatrix matrix = ConsoleMatrix.newMatrix(72, 16, false);
        ConsoleMatrix[] columns = matrix.splitHorizontal(new int[]{22, 22, 28});
        PrintWriter column0 = columns[0].getPrintWriter();
        PrintWriter column1 = columns[1].getPrintWriter();
        PrintWriter column2 = columns[2].getPrintWriter();

        column0.println("People online: " + this.lobbyUsers.size());
        this.lobbyUsers.forEach(column0::println);

        column1.println("Rooms available: " + this.lobbyRooms.size());
        this.lobbyRooms.forEach(column1::println);

        if (this.lastRoomName != null) {
            column2.println("You left room " + this.lastRoomName);
            this.lastRoomName = this.getCurrentRoomName();
        }
        if (this.getCurrentRoomName() != null) {
            if (this.getCurrentRoomName().equals(this.getUserName())) {
                column2.println("You hosted a room.");
            } else {
                column2.println("You joined the room of " + this.getCurrentRoomName());
            }
            column2.println("Room players: " + this.playersInTheRoom.size());
            this.playersInTheRoom.forEach(column2::println);
        }

        this.output.println(matrix.toString());

        if (!this.messages.isEmpty()) {
            this.output.println("You received " + this.messages.size() + " messages: ");
            this.messages.forEach(this.output::println);
            this.output.println();
            this.messages.clear();
        }
    }

    @Override
    public synchronized void displayAvailableRooms(Collection<String> roomNames) {
        this.lobbyRooms.clear();
        this.lobbyRooms.addAll(roomNames);
    }

    @Override
    public synchronized void displayUserList(Collection<String> userNames) {
        this.lobbyUsers.clear();
        this.lobbyUsers.addAll(userNames);
    }

    @Override
    public synchronized void notifyMessage(String author, String message) {
        this.messages.add("[" + author + "]: " + message);
    }

    @Override
    public synchronized void notifyRoomChanged(String newRoomName) {
        this.lastRoomName = this.getCurrentRoomName();
        this.currentRoomName = newRoomName;
        this.playersInTheRoom.clear();
    }

    @Override
    public synchronized void displayRoomPlayerList(Collection<String> playerList) {
        this.playersInTheRoom.clear();
        this.playersInTheRoom.addAll(playerList);
    }

    @Override
    public synchronized void notifyGameStarted(GameController gameController) {
        this.gameController = gameController;
    }
}

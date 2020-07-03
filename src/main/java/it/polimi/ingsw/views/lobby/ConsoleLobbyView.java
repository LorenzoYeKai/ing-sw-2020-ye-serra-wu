package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.views.utils.ConsoleMatrix;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;

public class ConsoleLobbyView implements LobbyView {
    private final PrintStream output;
    private final Set<String> lobbyUsers;
    private final Set<String> lobbyRooms;
    private final List<String> playersInTheRoom;
    private final List<String> messages;
    private final LobbyController controller;
    private final String userName;
    private final UserToken token;

    private final Consumer<GameController> onGameStarted;

    private boolean gameStarting = false;
    private String lastRoomName;
    private String currentRoomName;

    public ConsoleLobbyView(String userName,
                            LobbyController controller,
                            PrintStream output,
                            Consumer<GameController> onGameStarted)
            throws NotExecutedException, IOException {
        this.output = output;
        this.lobbyUsers = new TreeSet<>();
        this.lobbyRooms = new TreeSet<>();
        this.playersInTheRoom = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.controller = controller;
        this.userName = userName;
        this.token = this.controller.joinLobby(this.userName, this);
        this.onGameStarted = onGameStarted;

        this.lastRoomName = null;
        this.currentRoomName = null;
    }

    public String getUserName() {
        return this.userName;
    }

    public List<String> getRoomPlayers() {
        return this.playersInTheRoom;
    }

    public void displaySummary() {
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
            this.lastRoomName = this.currentRoomName;
        }
        if (this.currentRoomName != null) {
            if (this.currentRoomName.equals(this.getUserName())) {
                column2.println("You hosted a room.");
            } else {
                column2.println("You joined the room of " + this.currentRoomName);
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

    public void displayInputHint() {
        if (this.currentRoomName == null) {
            // if not inside room
            this.output.println("Write `host room` to host room");
            this.output.println("Write `join [Room Name]` to join room");
        } else {
            // if inside room
            this.output.println("Write `leave room` to leave room");
            if (this.userName.equals(this.currentRoomName)) {
                this.output.println("You are host of this room.");
                this.output.println("Write `up [Name]` to move someone up");
                this.output.println("Write `down [Name]` to move someone down");
                this.output.println("Write `kick [Name]` to kick someone");
                this.output.println("Write `start game` to start the game");
            }
        }
    }

    public void executeInput(String command, String data)
            throws NotExecutedException, IOException {
        switch (command) {
            case "host" -> this.controller.createRoom(this.token);
            case "join" -> this.controller.joinRoom(this.token, data);
            case "leave" -> this.controller.leaveRoom(this.token);
            case "up" -> this.controller.changePlayerPosition(this.token, data, -1);
            case "down" -> this.controller.changePlayerPosition(this.token, data, +1);
            case "kick" -> this.controller.kickUser(this.token, data);
            case "start" -> this.controller.startGame(this.token);
            default -> this.output.println("Unknown action, skipped");
        }
    }

    @Override
    public void displayAvailableRooms(Collection<String> roomNames) {
        this.lobbyRooms.clear();
        this.lobbyRooms.addAll(roomNames);
    }

    @Override
    public void displayUserList(Collection<String> userNames) {
        this.lobbyUsers.clear();
        this.lobbyUsers.addAll(userNames);
    }

    @Override
    public void notifyMessage(String author, String message) {
        this.messages.add("[" + author + "]: " + message);
    }

    @Override
    public void notifyRoomChanged(String newRoomName) {
        this.lastRoomName = this.currentRoomName;
        this.currentRoomName = newRoomName;
        if (!gameStarting) {
            this.playersInTheRoom.clear();
        }
    }

    @Override
    public void displayRoomPlayerList(Collection<String> playerList) {
        if (!gameStarting) {
            this.playersInTheRoom.clear();
            this.playersInTheRoom.addAll(playerList);
        }
    }

    @Override
    public void notifyGameStarted(GameController gameController) {
        this.gameStarting = true;
        this.onGameStarted.accept(gameController);
    }
}

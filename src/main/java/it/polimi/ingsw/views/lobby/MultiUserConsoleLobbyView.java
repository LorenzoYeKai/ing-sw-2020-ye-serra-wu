package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.lobby.RoomData;
import it.polimi.ingsw.models.lobby.UserData;
import it.polimi.ingsw.views.game.GameView;
import it.polimi.ingsw.views.game.MultiUserConsoleGameView;
import it.polimi.ingsw.views.utils.ConsoleMatrix;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a console-based View of lobby,
 * for multiple users (so multiple users will have the same View),
 * which will use the view by turn.
 */
public class MultiUserConsoleLobbyView {
    private final LobbyController controller;
    private final Scanner input;
    private final PrintStream output;
    private final List<SharedConsoleLobbyView> views;
    private GameController gameController;

    public MultiUserConsoleLobbyView(LobbyController controller) throws NotExecutedException {
        this.controller = controller;
        this.input = new Scanner(System.in);
        this.output = System.out;
        this.output.println("Type your names, end with an empty line.");
        this.views = new ArrayList<>();

        String username = this.input.nextLine();
        // Read first username
        while (username.isEmpty()) {
            this.output.println("Please type at least one username");
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

    public List<String> getUserNames() {
        return this.views.stream()
                .map(view -> view.getUser().getUsername())
                .collect(Collectors.toUnmodifiableList());
    }

    public MultiUserConsoleGameView getUserInputUntilGameStarts() throws NotExecutedException {
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
            if(view.getGameController() == this.gameController) {
                gameView.join(view.getUser().getUsername());
            }
        }
        return gameView;
    }

    private void getUserInput(SharedConsoleLobbyView view) {
        while (true) {
            UserData user = view.getUser();
            this.output.println("Now it's turn of " + user.getUsername());
            this.output.println("Summary: ");
            view.displaySummary();
            this.output.println();

            if (view.getCurrentRoom() == null) {
                // if not inside room
                this.output.println("Write `host [Name]` to host room");
                this.output.println("Write `join [Room ID]` to join room");
            } else {
                // if inside room
                this.output.println("Write `leave room` to leave room");
                if (user == view.getCurrentRoom().getHost()) {
                    this.output.println("You are host of room " + view.getCurrentRoom().getRoomName());
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
                    case "host":
                        this.controller.createRoom(user, parameter);
                        break;
                    case "join":
                        this.controller.joinRoom(user, view.findRoom(parameter));
                        break;
                    case "leave":
                        this.controller.leaveRoom(user, view.getCurrentRoom());
                        break;
                    case "up":
                        this.controller.changePlayerPosition(user,
                                view.getCurrentRoom(),
                                view.findPlayersInTheRoom(parameter),
                                -1);
                        break;
                    case "down":
                        this.controller.changePlayerPosition(user,
                                view.getCurrentRoom(),
                                view.findPlayersInTheRoom(parameter),
                                +1);
                        break;
                    case "kick":
                        this.controller.kickUser(user,
                                view.getCurrentRoom(),
                                view.findPlayersInTheRoom(parameter));
                        break;
                    case "start":
                        this.controller.startGame(user, view.getCurrentRoom());
                        break;
                    default:
                        this.output.println("Unknown action, skipped");
                        break;
                }
            } catch (NotExecutedException exception) {
                this.output.println("Operation rejected: " + exception.getMessage());
            }

            if (view.getGameController() != null) {
                this.output.println("Switching to GameView...");
                this.gameController = view.getGameController();
                return;
            }
        }
    }

}

class SharedConsoleLobbyView extends LobbyView {
    private final PrintStream output;
    private final List<UserData> users;
    private final Map<Integer, RoomData> rooms;
    private final UserData user;
    private RoomData lastRoom;
    private RoomData currentRoom;
    private final Map<String, UserData> playersInTheRoom;
    private final List<String> messages;
    private GameController gameController;

    public SharedConsoleLobbyView(LobbyController controller,
                                  String username,
                                  PrintStream output) throws NotExecutedException {
        super(controller);
        this.output = output;
        this.users = new ArrayList<>();
        this.rooms = new HashMap<>();
        this.user = controller.joinLobby(username, this);
        this.lastRoom = null;
        this.currentRoom = null;
        this.playersInTheRoom = new HashMap<>();
        this.messages = new ArrayList<>();
        this.gameController = null;
    }

    public UserData getUser() {
        return this.user;
    }

    public RoomData getCurrentRoom() {
        return this.currentRoom;
    }

    public GameController getGameController() {
        return this.gameController;
    }

    public RoomData findRoom(String roomId) throws NotExecutedException {
        int id;
        try {
            id = Integer.parseInt(roomId);
        } catch (NumberFormatException exception) {
            throw new NotExecutedException("Invalid room id");
        }

        if (!this.rooms.containsKey(id)) {
            throw new NotExecutedException("Invalid room id");
        }
        return this.rooms.get(id);
    }

    public UserData findPlayersInTheRoom(String name) throws NotExecutedException {
        if (this.currentRoom == null) {
            throw new NotExecutedException("We are even not inside a room");
        }
        if (!this.playersInTheRoom.containsKey(name)) {
            throw new NotExecutedException("This player is not inside the room");
        }
        return this.playersInTheRoom.get(name);
    }

    public void displaySummary() {
        ConsoleMatrix matrix = ConsoleMatrix.newMatrix(72, 16, false);
        ConsoleMatrix[] columns = matrix.splitHorizontal(new int[]{22,22,28});
        PrintWriter column0 = columns[0].getPrintWriter();
        PrintWriter column1 = columns[1].getPrintWriter();
        PrintWriter column2 = columns[2].getPrintWriter();

        column0.println("People online: " + this.users.size());
        this.users.forEach(user -> column0.println(user.getUsername()));

        column1.println("Rooms available: " + this.rooms.size());
        this.rooms.forEach((id, room) -> {
            column1.println("Room name: " + room.getRoomName());
            column1.println("Room id: " + room.getRoomId());
            column1.println("Host: " + room.getHost().getUsername());
        });

        if(this.lastRoom != null) {
            column2.println("You left room " + this.lastRoom.getRoomName());
            this.lastRoom = this.getCurrentRoom();
        }
        if(this.currentRoom != null) {
            if(this.user == this.currentRoom.getHost()) {
                column2.println("You hosted room " + this.currentRoom.getRoomName());
            }
            else {
                column2.println("You joined room" + this.currentRoom.getRoomName());
            }
            column2.println("Room players: " + this.playersInTheRoom.size());
            this.playersInTheRoom.forEach((username, user) -> column2.println(username));
        }

        this.output.println(matrix.toString());

        if(!this.messages.isEmpty()) {
            this.output.println("You received " + this.messages.size() + " messages: ");
            this.messages.forEach(this.output::println);
            this.output.println();
        }
    }

    @Override
    public void displayAvailableRooms(Collection<RoomData> rooms) {
        this.rooms.clear();
        rooms.forEach(room -> this.rooms.put(room.getRoomId(), room));
    }

    @Override
    public void displayUserList(Collection<UserData> users) {
        this.users.clear();
        this.users.addAll(users);
    }

    @Override
    public void notifyMessage(String author, String message) {
        this.messages.add("[" + author + "]: " + message);
    }

    @Override
    public void notifyRoomChanged(RoomData roomData) {
        this.lastRoom = this.currentRoom;
        this.currentRoom = roomData;
        this.playersInTheRoom.clear();
    }

    @Override
    public void displayRoomPlayerList(Collection<UserData> playerList) {
        this.playersInTheRoom.clear();
        playerList.forEach(user -> this.playersInTheRoom.put(user.getUsername(), user));
    }

    @Override
    public void notifyGameStarted(GameController gameController) {
        this.gameController = gameController;
    }
}

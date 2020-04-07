package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.lobby.RoomData;
import it.polimi.ingsw.models.lobby.UserData;
import it.polimi.ingsw.views.game.MultiUserConsoleGameView;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

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

    public GameController getUserInputUntiGameStarts() {
        while (true) {
            for (SharedConsoleLobbyView view : views) {
                this.getUserInput(view);
                if(this.gameController != null) {
                    return this.gameController;
                }
            }
        }
    }

    private void getUserInput(SharedConsoleLobbyView view) {
        while (true) {
            UserData user = view.getUser();
            this.output.println("Now it's turn of " + user.getUsername());
            if (user.getCurrentRoom() == null) {
                // if not inside room
                this.output.println("Write `host [Name]` to host room");
                this.output.println("Write `join [Room ID]` to join room");
            } else {
                // if inside room
                this.output.println("Write `leave room` to leave room");
                if (user == user.getCurrentRoom().getHost()) {
                    this.output.println("You are host of room " + user.getCurrentRoom().getRoomName());
                    this.output.println("Write `up [Name]` to move someone up");
                    this.output.println("Write `down [Name]` to move someone down");
                    this.output.println("Write `kick [Name]` to kick someone");
                    this.output.println("Write `start game` to start the game");
                }
            }
            this.output.println("Write empty line (pressing ENTER) to end your turn.");
            String line = this.input.nextLine();
            if (line.isEmpty()) {
                return;
            }

            String[] splitted = line.split("\\s+", 2);
            if (splitted.length != 2) {
                this.output.println("Wrong input, press ENTER to try again!");
                this.input.nextLine();
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
                        this.controller.leaveRoom(user);
                        break;
                    case "up":
                        this.controller.changePlayerPosition(user,
                                view.findPlayersInTheRoom(parameter),
                                -1);
                        break;
                    case "down":
                        this.controller.changePlayerPosition(user,
                                view.findPlayersInTheRoom(parameter),
                                +1);
                        break;
                    case "kick":
                        this.controller.kickUser(user, view.findPlayersInTheRoom(parameter));
                        break;
                    case "start":
                        this.controller.startGame(user);
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

            this.output.println("Press ENTER to continue your turn");
            this.input.nextLine();
        }
    }

}

class SharedConsoleLobbyView extends LobbyView {
    private final String username;
    private final PrintStream output;
    private final UserData user;
    private final Map<Integer, RoomData> rooms;
    private final Map<String, UserData> playersInTheRoom;
    private GameController gameController;

    public SharedConsoleLobbyView(LobbyController controller,
                                  String username,
                                  PrintStream output) throws NotExecutedException {
        super(controller);
        this.username = username;
        this.output = output;
        this.user = controller.joinLobby(username, this);
        this.rooms = new HashMap<>();
        this.playersInTheRoom = new HashMap<>();
    }

    public String getUsername() {
        return this.username;
    }

    public UserData getUser() {
        return this.user;
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
        if (this.user.getCurrentRoom() == null) {
            throw new NotExecutedException("We are even not inside a room");
        }
        if (!this.playersInTheRoom.containsKey(name)){
            throw new NotExecutedException("This player is not inside the room");
        }
        return this.playersInTheRoom.get(name);
    }

    private void beginOutput() {
        this.output.println("BEGIN output for user " + this.username);
    }

    private void endOutput() {
        this.output.println("END output for user " + this.username + "\n");
    }

    @Override
    public void displayAvailableRooms(Collection<RoomData> rooms) {
        this.beginOutput();
        this.output.println("Available rooms (" + rooms.size() + "): ");
        this.rooms.clear();
        for (RoomData room : rooms) {
            this.output.println("Room name: " + room.getRoomName() + "; Room id: " + room.getRoomId());
            this.output.println("Host name: " + room.getHost().getUsername() + "; Number of people: " + room.getNumberOfUsers());
            this.rooms.put(room.getRoomId(), room);
        }
        this.endOutput();
    }

    @Override
    public void displayUserList(Set<String> users) {
        this.beginOutput();
        this.output.println("Online user list (" + users.size() + "): ");
        for (String user : users) {
            this.output.println(user);
        }
        this.endOutput();
    }

    @Override
    public void displayLastMessage(String author, String message) {
        this.beginOutput();
        this.output.println("[" + author + "]: " + message);
        this.endOutput();
    }

    @Override
    public void notifyHostedRoom(RoomData roomData) {
        this.beginOutput();
        this.output.println("You have hosted the room: " + roomData.getRoomName());
        this.endOutput();
    }

    @Override
    public void notifyJoinedRoom(RoomData roomData) {
        this.beginOutput();
        this.output.println("You have joined the room: " + roomData.getRoomName());
        this.endOutput();
    }

    @Override
    public void displayRoomPlayerList(List<UserData> playerList) {
        this.beginOutput();
        this.output.println("Room player list (" + playerList.size() + "): ");
        this.playersInTheRoom.clear();
        for (UserData player : playerList) {
            this.output.println(player.getUsername());
            this.playersInTheRoom.put(player.getUsername(), player);
        }
        this.endOutput();
    }

    @Override
    public void notifyLeftRoom(RoomData roomData) {
        this.beginOutput();
        this.playersInTheRoom.clear();
        this.output.println("You have left the room: " + roomData.getRoomName());
        this.endOutput();
    }

    @Override
    public void notifyGameStarted(GameController gameController) {
        this.beginOutput();
        this.output.println("Game has started!");
        this.gameController = gameController;
        this.endOutput();
    }
}

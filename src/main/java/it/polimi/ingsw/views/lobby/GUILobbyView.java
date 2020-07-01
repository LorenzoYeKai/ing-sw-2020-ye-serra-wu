package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.GUI.LobbyGUIController;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.lobby.UserToken;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class GUILobbyView implements LobbyView {



    private final Set<String> lobbyUsers;
    private final Set<String> lobbyRooms;
    private final List<String> playersInTheRoom;
    private final LobbyController controller;
    private final String userName;
    private final UserToken token;
    private LobbyGUIController lobbyGUIController;

    private final Consumer<GameController> onGameStarted;

    private String lastRoomName;
    private String currentRoomName;

    public GUILobbyView(String userName,
                            LobbyController controller,
                            LobbyGUIController lobbyGUIController,
                            Consumer<GameController> onGameStarted)
            throws NotExecutedException, IOException {
        this.lobbyUsers = new TreeSet<>();
        this.lobbyRooms = new TreeSet<>();
        this.lobbyGUIController = lobbyGUIController;
        this.lobbyGUIController.setView(this);
        this.playersInTheRoom = new ArrayList<>();
        this.controller = controller;
        this.userName = userName;
        this.token = this.controller.joinLobby(this.userName, this);
        this.onGameStarted = onGameStarted;

        this.lastRoomName = null;
        this.currentRoomName = null;
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
            default -> System.out.println("Unknown action, skipped");
        }
    }

    public List<String> getPlayersInTheRoom(){
        return this.playersInTheRoom;
    }

    public LobbyGUIController getLobbyGUIController(){
        return this.lobbyGUIController;
    }

    public Set<String> getLobbyUsers() {
        return lobbyUsers;
    }

    public Set<String> getLobbyRooms() {
        return lobbyRooms;
    }

    public void setLobbyGUIController(LobbyGUIController lobbyGUIController){
        this.lobbyGUIController = lobbyGUIController;
    }


    @Override
    public void displayAvailableRooms(Collection<String> roomNames) {
        this.lobbyRooms.clear();
        this.lobbyRooms.addAll(roomNames);
        lobbyGUIController.setAvailableRooms(lobbyRooms);
        lobbyGUIController.updateAvailableRooms();
        roomNames.forEach(System.out::println);
    }

    @Override
    public void displayUserList(Collection<String> userNames) {
        this.lobbyUsers.clear();
        this.lobbyUsers.addAll(userNames);
        lobbyGUIController.setOnlinePlayers(lobbyUsers);
        lobbyGUIController.updateOnlinePlayers();
        //output.addPlayer("ciao");
        //output.updateOnlinePlayers(userNames);
        userNames.forEach(System.out::println);
    }

    @Override
    public void notifyMessage(String author, String message) {
        System.out.println("[" + author + "]: " + message);
        lobbyGUIController.receiveMessage("[" + author + "]: " + message);
    }

    @Override
    public void notifyRoomChanged(String newRoomName) {
        this.lastRoomName = this.currentRoomName;
        this.currentRoomName = newRoomName;
        this.playersInTheRoom.clear();
        System.out.println("Current room: " + currentRoomName);
    }

    @Override
    public void displayRoomPlayerList(Collection<String> playerList) {
        this.playersInTheRoom.clear();
        this.playersInTheRoom.addAll(playerList);
        this.lobbyGUIController.updatePlayersInTheRoom();

    }

    @Override
    public void notifyGameStarted(GameController gameController) {

    }


}

package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.GUI.LobbyGUIController;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.views.lobby.LobbyView;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;

public class GUILobbyView implements LobbyView {

    private final Set<String> lobbyUsers;
    private final Set<String> lobbyRooms;
    private final List<String> playersInTheRoom;
    private final LobbyController controller;
    private final String userName;
    private final UserToken token;
    private final LobbyGUIController output;

    private final Consumer<GameController> onGameStarted;

    private String lastRoomName;
    private String currentRoomName;

    public GUILobbyView(String userName,
                            LobbyController controller,
                            LobbyGUIController output,
                            Consumer<GameController> onGameStarted)
            throws NotExecutedException, IOException {
        this.lobbyUsers = new TreeSet<>();
        this.lobbyRooms = new TreeSet<>();
        this.output = output;
        this.playersInTheRoom = new ArrayList<>();
        this.controller = controller;
        this.userName = userName;
        this.token = this.controller.joinLobby(this.userName, this);
        this.onGameStarted = onGameStarted;

        this.lastRoomName = null;
        this.currentRoomName = null;
    }

    @Override
    public void displayAvailableRooms(Collection<String> roomNames) {

    }

    @Override
    public void displayUserList(Collection<String> userNames) {

        output.addPlayer("ciao");
        //output.updateOnlinePlayers(userNames);
        userNames.forEach(System.out::println);
    }

    @Override
    public void notifyMessage(String author, String message) {

    }

    @Override
    public void notifyRoomChanged(String newRoomName) {

    }

    @Override
    public void displayRoomPlayerList(Collection<String> playerList) {

    }

    @Override
    public void notifyGameStarted(GameController gameController) {

    }


}

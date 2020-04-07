package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.lobby.RoomData;
import it.polimi.ingsw.models.lobby.UserData;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class LobbyView {
    protected final LobbyController controller;

    protected LobbyView(LobbyController controller) {
        this.controller = controller;
    }

    public abstract void displayAvailableRooms(Collection<RoomData> rooms);

    public abstract void displayUserList(Set<String> users);

    public abstract void displayLastMessage(String author, String message);

    public abstract void notifyHostedRoom(RoomData roomData);

    public abstract void notifyJoinedRoom(RoomData roomData);

    public abstract void displayRoomPlayerList(List<UserData> playerList);

    public abstract void notifyLeftRoom(RoomData roomData);

    public abstract void notifyGameStarted(GameController gameController);
}

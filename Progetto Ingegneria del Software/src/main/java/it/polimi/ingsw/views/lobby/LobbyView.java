package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.lobby.RoomData;
import it.polimi.ingsw.models.lobby.UserData;

import java.util.Collection;

public abstract class LobbyView {
    protected final LobbyController controller;

    protected LobbyView(LobbyController controller) {
        this.controller = controller;
    }

    public abstract void displayAvailableRooms(Collection<RoomData> rooms);

    public abstract void displayUserList(Collection<UserData> users);

    public abstract void notifyMessage(String author, String message);

    public abstract void notifyRoomChanged(RoomData roomData);

    public abstract void displayRoomPlayerList(Collection<UserData> playerList);

    public abstract void notifyGameStarted(GameController gameController);
}

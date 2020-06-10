package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;

import java.util.Collection;

public abstract class LobbyView {
    protected final LobbyController controller;

    protected LobbyView(LobbyController controller) {
        this.controller = controller;
    }

    public abstract void displayAvailableRooms(Collection<String> roomNames);

    public abstract void displayUserList(Collection<String> userNames);

    public abstract void notifyMessage(String author, String message);

    public abstract void notifyRoomChanged(String newRoomName);

    public abstract void displayRoomPlayerList(Collection<String> playerList);

    public abstract void notifyGameStarted(GameController gameController);
}

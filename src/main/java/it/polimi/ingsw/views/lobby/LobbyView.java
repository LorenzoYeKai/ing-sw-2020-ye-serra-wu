package it.polimi.ingsw.views.lobby;

import it.polimi.ingsw.controller.game.GameController;

import java.util.Collection;

public interface LobbyView {

    void displayAvailableRooms(Collection<String> roomNames);

    void displayUserList(Collection<String> userNames);

    void notifyMessage(String author, String message);

    void notifyRoomChanged(String newRoomName);

    void displayRoomPlayerList(Collection<String> playerList);

    void notifyGameStarted(GameController gameController);
}

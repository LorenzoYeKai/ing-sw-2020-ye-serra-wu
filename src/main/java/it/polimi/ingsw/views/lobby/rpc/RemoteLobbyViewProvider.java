package it.polimi.ingsw.views.lobby.rpc;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.rpc.RequestProcessor;
import it.polimi.ingsw.views.lobby.LobbyView;

import java.io.IOException;
import java.util.Collection;


public class RemoteLobbyViewProvider implements LobbyView {
    private final RequestProcessor connection;
    private boolean valid = true;

    public RemoteLobbyViewProvider(RequestProcessor connection) {
        this.connection = connection;
    }

    @Override
    public void displayAvailableRooms(Collection<String> roomNames) {
        StringsCommandType type = StringsCommandType.DISPLAY_AVAILABLE_ROOMS;
        this.connection.remoteNotify(new StringsMessage(type, roomNames));
    }

    @Override
    public void displayUserList(Collection<String> userNames) {
        StringsCommandType type = StringsCommandType.DISPLAY_USER_LIST;
        this.connection.remoteNotify(new StringsMessage(type, userNames));
    }

    @Override
    public void notifyMessage(String author, String message) {
        this.connection.remoteNotify(new TextMessage(author, message));
    }

    @Override
    public void notifyRoomChanged(String newRoomName) {
        this.connection.remoteNotify(new RoomChangedMessage(newRoomName));
    }

    @Override
    public void displayRoomPlayerList(Collection<String> playerList) {
        StringsCommandType type = StringsCommandType.DISPLAY_ROOM_PLAYER_LIST;
        this.connection.remoteNotify(new StringsMessage(type, playerList));
    }

    @Override
    public void notifyGameStarted(GameController gameController) {
        this.connection.remoteNotify(new GameStartedMessage());
    }

    private void onIOException(IOException e) {
        this.valid = false;
        // TODO: handle io exception
    }
}

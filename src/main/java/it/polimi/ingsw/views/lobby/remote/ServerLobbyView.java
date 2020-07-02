package it.polimi.ingsw.views.lobby.remote;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.remote.ServerGameController;
import it.polimi.ingsw.requests.RequestProcessor;
import it.polimi.ingsw.views.lobby.LobbyView;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Represents the server side of lobby connection. As the name suggests, it
 * should be instantiated at the server side.
 * On the server, it's used like a normal {@link LobbyView}, but it actually
 * forwards all notifications to the actual client through the connected
 * {@link ClientLobbyView}.
 */
public class ServerLobbyView implements LobbyView {
    private final RequestProcessor connection;
    private boolean valid = true;

    public ServerLobbyView(RequestProcessor connection) {
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
        // Create the Server-side wrapper of GameController for this user
        ServerGameController serverGameController =
                new ServerGameController(this.connection, gameController);

        Runnable action = () -> {
            // Add the ServerGameController to the RequestProcessor so it will be
            // able to receive client messages.
            this.connection.addHandler(serverGameController);
            // After receiving the GameStartedMessage, the client can create a
            // ClientGameController which will connect to the ServerGameController.
            this.connection.remoteNotify(new GameStartedMessage());
        };

        if(this.connection.isOnEventThread()) {
            action.run();
        }
        else {
            CompletableFuture<Void> task = new CompletableFuture<>();
            // ensure thread safety, because notifyGameStarted
            // might be called from another RequestProcessor (of host)
            this.connection.invokeAsync(() -> {
                action.run();
                task.complete(null);
            });
            // wait until task finishes
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new InternalError(e);
            }
        }
    }

    private void onIOException(IOException e) {
        this.valid = false;
        // TODO: handle io exception
    }
}

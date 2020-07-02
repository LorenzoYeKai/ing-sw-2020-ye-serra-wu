package it.polimi.ingsw.controller.lobby.remote;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.requests.RequestProcessor;
import it.polimi.ingsw.views.lobby.LobbyView;
import it.polimi.ingsw.views.lobby.remote.ClientLobbyView;

import java.io.IOException;

import static it.polimi.ingsw.controller.lobby.remote.RemoteCommandType.*;

/**
 * Represents the client-side endpoint of lobby connection.
 * i.e. it should be instantiated at the client side and it should be used as
 * a LobbyController.
 * @see ServerLobbyController
 * @see LobbyController
 */
public class ClientLobbyController implements LobbyController {
    private final RequestProcessor connection;
    private ClientLobbyView view;

    public ClientLobbyController(RequestProcessor connection) {
        this.connection = connection;
    }


    @Override
    public UserToken joinLobby(String username, LobbyView view)
            throws NotExecutedException, IOException {
        if (this.view != null) {
            throw new InternalError("ClientLobbyController already being used");
        }
        this.view = new ClientLobbyView(connection, view);
        this.connection.addHandler(this.view);
        JoinCommand command = new JoinCommand(username);
        try {
            return (UserToken) this.connection.remoteInvoke(command);
        } catch (Throwable e) {
            // cleanup: remove handlers
            this.connection.removeHandler(this.view);
            this.view = null;
            throw e;
        }
    }

    @Override
    public void leaveLobby(UserToken userToken)
            throws NotExecutedException, IOException {
        this.connection.remoteInvoke(new UserCommand(LEAVE_LOBBY, userToken));
        this.connection.removeHandler(this.view);
        this.view = null;
    }

    @Override
    public void createRoom(UserToken userToken)
            throws NotExecutedException, IOException {
        this.connection.remoteInvoke(new UserCommand(CREATE_ROOM, userToken));
    }

    @Override
    public void joinRoom(UserToken userToken, String roomName)
            throws NotExecutedException, IOException {
        var command = new UserParameterCommand(JOIN_ROOM, userToken, roomName);
        this.connection.remoteInvoke(command);
    }

    @Override
    public void leaveRoom(UserToken userToken)
            throws NotExecutedException, IOException {
        this.connection.remoteInvoke(new UserCommand(LEAVE_ROOM, userToken));
    }

    @Override
    public void changePlayerPosition(UserToken hostToken,
                                     String targetUserName,
                                     int offset)
            throws NotExecutedException, IOException {
        var command = new ChangePlayerPositionCommand(hostToken, targetUserName, offset);
        this.connection.remoteInvoke(command);
    }

    @Override
    public void kickUser(UserToken hostToken, String targetUserName)
            throws NotExecutedException, IOException {
        var command = new UserParameterCommand(KICK_USER, hostToken, targetUserName);
        this.connection.remoteInvoke(command);
    }

    @Override
    public void startGame(UserToken hostToken)
            throws NotExecutedException, IOException {
        this.connection.remoteInvoke(new UserCommand(START_GAME, hostToken));
    }
}

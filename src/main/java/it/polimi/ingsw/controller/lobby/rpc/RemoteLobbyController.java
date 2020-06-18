package it.polimi.ingsw.controller.lobby.rpc;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.InternalError;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.rpc.RequestProcessor;
import it.polimi.ingsw.views.lobby.LobbyView;
import it.polimi.ingsw.views.lobby.rpc.RemoteLobbyView;

import java.io.IOException;

import static it.polimi.ingsw.controller.lobby.rpc.RemoteCommandType.*;


public class RemoteLobbyController implements LobbyController {
    private final RequestProcessor connection;
    private RemoteLobbyView view;

    public RemoteLobbyController(RequestProcessor connection) {
        this.connection = connection;
    }


    @Override
    public UserToken joinLobby(String username, LobbyView view)
            throws NotExecutedException, IOException {
        if (this.view != null) {
            throw new InternalError("RemoteLobbyController already being used");
        }
        this.view = new RemoteLobbyView(view);
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

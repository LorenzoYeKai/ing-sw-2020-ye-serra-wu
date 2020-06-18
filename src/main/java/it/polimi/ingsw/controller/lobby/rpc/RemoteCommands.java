package it.polimi.ingsw.controller.lobby.rpc;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.views.lobby.LobbyView;

import java.io.IOException;
import java.io.Serializable;

/**
 * The type of {@link RemoteCommand}
 */
enum RemoteCommandType {
    LEAVE_LOBBY,
    CREATE_ROOM,
    JOIN_ROOM,
    LEAVE_ROOM,
    KICK_USER,
    CHANGE_PLAYER_POSITION,
    START_GAME
}

/**
 * A serializable class which contains all useful data to invoke a method on
 * another {@link LobbyController}.
 */
abstract class RemoteCommand implements Serializable {
    protected final RemoteCommandType type;

    protected RemoteCommand(RemoteCommandType type) {
        this.type = type;
    }

    public abstract void apply(LobbyController controller)
            throws NotExecutedException, IOException;
}

/**
 * A special command which corresponds to
 * {@link LobbyController#joinLobby(String, LobbyView)}
 */
final class JoinCommand implements Serializable {
    private final String username;

    public JoinCommand(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}

class UserCommand extends RemoteCommand {
    protected final UserToken token;

    public UserCommand(RemoteCommandType type, UserToken token) {
        super(type);
        this.token = token;
    }

    @Override
    public void apply(LobbyController controller)
            throws NotExecutedException, IOException {
        switch (this.type) {
            case LEAVE_LOBBY -> controller.leaveLobby(this.token);
            case CREATE_ROOM -> controller.createRoom(this.token);
            case LEAVE_ROOM -> controller.leaveRoom(this.token);
            case START_GAME -> controller.startGame(this.token);
            default -> throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }
}

class UserParameterCommand extends UserCommand {
    protected final String parameter;

    public UserParameterCommand(RemoteCommandType type,
                                UserToken token, String parameter) {
        super(type, token);
        this.parameter = parameter;
    }

    @Override
    public void apply(LobbyController controller)
            throws NotExecutedException, IOException {
        switch (this.type) {
            case JOIN_ROOM -> controller.joinRoom(this.token, this.parameter);
            case KICK_USER -> controller.kickUser(this.token, this.parameter);
            default -> throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }
}

final class ChangePlayerPositionCommand extends UserParameterCommand {
    private final int offset;

    protected ChangePlayerPositionCommand(UserToken token,
                                          String targetUserName,
                                          int offset) {
        super(RemoteCommandType.CHANGE_PLAYER_POSITION, token, targetUserName);
        this.offset = offset;
    }

    @Override
    public void apply(LobbyController controller)
            throws NotExecutedException, IOException {
        controller.changePlayerPosition(this.token, this.parameter, this.offset);
    }
}
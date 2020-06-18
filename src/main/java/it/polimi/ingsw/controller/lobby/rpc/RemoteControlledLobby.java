package it.polimi.ingsw.controller.lobby.rpc;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.rpc.RemoteCommandHandler;
import it.polimi.ingsw.rpc.RequestProcessor;
import it.polimi.ingsw.views.lobby.LobbyView;
import it.polimi.ingsw.views.lobby.rpc.RemoteLobbyViewProvider;

import java.io.IOException;
import java.io.Serializable;

/**
 * The {@link RemoteControlledLobby} accepts inputs from ClientLobbyController by
 * using sockets, and forwards them to the underlying {@link LobbyController}
 */
public class RemoteControlledLobby implements RemoteCommandHandler, AutoCloseable {
    private final RequestProcessor requestProcessor;
    private final LobbyController controller;
    private UserToken token = null;

    public RemoteControlledLobby(RequestProcessor requestProcessor,
                                 LobbyController underlyingController) {
        this.requestProcessor = requestProcessor;
        this.controller = underlyingController;
    }

    @Override
    public void close() {
        // if user hasn't left the lobby when close() is called, very probably
        // because an exceptional situation, then let's call the leave for the
        // user.
        System.err.println("Close has been called, token = " + this.token);
        if (this.token != null) {
            try {
                System.err.println("Trying to leave lobby with " + this.token);
                this.controller.leaveLobby(this.token);
            } catch (NotExecutedException | IOException ignored) {
                System.err.println("Exception: " + ignored);
            }
        }
    }

    @Override
    public boolean isProcessable(Object command) {
        return command instanceof RemoteCommand || command instanceof JoinCommand;
    }

    @Override
    public Serializable processCommand(Object command) throws NotExecutedException {
        assert isProcessable(command);
        if (command instanceof JoinCommand) {
            String userName = ((JoinCommand) command).getUsername();
            LobbyView view = new RemoteLobbyViewProvider(this.requestProcessor);
            try {
                // saving token for cleanup in exceptional situations
                // (see close() method)
                this.token = this.controller.joinLobby(userName, view);
                return this.token;
            } catch (IOException e) {
                throw new InternalError("IOException when processing command");
            }
        }
        try {
            RemoteCommand remoteCommand = (RemoteCommand) command;
            remoteCommand.apply(this.controller);
            // after this command has been successfully applied, check:
            if (remoteCommand.type == RemoteCommandType.LEAVE_LOBBY) {
                // no need to leave lobby because the user has already left
                this.token = null;
            }
        } catch (IOException e) {
            throw new InternalError("IOException when processing command");
        }
        return null;
    }
}


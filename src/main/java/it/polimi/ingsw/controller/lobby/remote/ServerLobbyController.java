package it.polimi.ingsw.controller.lobby.remote;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.requests.RemoteRequestHandler;
import it.polimi.ingsw.requests.RequestProcessor;
import it.polimi.ingsw.views.lobby.LobbyView;
import it.polimi.ingsw.views.lobby.remote.ServerLobbyView;

import java.io.IOException;
import java.io.Serializable;

/**
 * The {@link ServerLobbyController} accepts inputs from {@link ClientLobbyController} by
 * using sockets, and forwards them to the underlying {@link LobbyController}
 */
public class ServerLobbyController implements RemoteRequestHandler, AutoCloseable {
    private final RequestProcessor requestProcessor;
    private final LobbyController controller;
    private UserToken token = null;

    public ServerLobbyController(RequestProcessor requestProcessor,
                                 LobbyController underlyingController) {
        this.requestProcessor = requestProcessor;
        this.controller = underlyingController;
    }

    @Override
    public void close() {
        // if user hasn't left the lobby when close() is called, very probably
        // because an exceptional situation, then let's call the leave for the
        // user.
        if (this.token != null) {
            try {
                this.controller.leaveLobby(this.token);
            } catch (NotExecutedException | IOException ignored) {
            }
        }
    }

    @Override
    public boolean isProcessable(Object input) {
        return input instanceof RemoteCommand || input instanceof JoinCommand;
    }

    @Override
    public Serializable processRequest(Object request) throws NotExecutedException {
        assert isProcessable(request);
        if (request instanceof JoinCommand) {
            String userName = ((JoinCommand) request).getUsername();
            LobbyView view = new ServerLobbyView(this.requestProcessor);
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
            RemoteCommand remoteCommand = (RemoteCommand) request;
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


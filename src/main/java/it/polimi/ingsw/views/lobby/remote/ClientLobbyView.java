package it.polimi.ingsw.views.lobby.remote;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.requests.RemoteRequestHandler;
import it.polimi.ingsw.views.lobby.LobbyView;

import java.io.Serializable;

/**
 * Represents the client-side endpoint of lobby connection. As the name
 * suggests, it should be instantiated at the client side.
 * It wraps a real underlying {@link LobbyView}, and forwards notifications to
 * it.
 * @see ServerLobbyView
 */
public class ClientLobbyView implements RemoteRequestHandler {
    private final LobbyView view;

    public ClientLobbyView(LobbyView underlyingView) {
        view = underlyingView;
    }

    @Override
    public boolean isProcessable(Object input) {
        return input instanceof Message || input instanceof GameStartedMessage;
    }

    @Override
    public Serializable processRequest(Object request) throws NotExecutedException {
        assert isProcessable(request);
        if (request instanceof GameStartedMessage) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
        ((Message) request).apply(this.view);
        return null;
    }
}

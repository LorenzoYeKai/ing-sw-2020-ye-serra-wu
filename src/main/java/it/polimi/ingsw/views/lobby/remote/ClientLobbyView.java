package it.polimi.ingsw.views.lobby.remote;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.remote.ClientGameController;
import it.polimi.ingsw.requests.RemoteRequestHandler;
import it.polimi.ingsw.requests.RequestProcessor;
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
    private final RequestProcessor processor;
    private final LobbyView view;

    public ClientLobbyView(RequestProcessor processor, LobbyView underlyingView) {
        this.processor = processor;
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
            GameController controller = new ClientGameController(processor);
            view.notifyGameStarted(controller);
        }
        else {
            ((Message) request).apply(this.view);
        }
        return null;
    }
}

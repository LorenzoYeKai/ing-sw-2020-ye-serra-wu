package it.polimi.ingsw.views.game.remote;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.requests.RemoteRequestHandler;
import it.polimi.ingsw.views.game.GameView;

import java.io.Serializable;

public class ClientGameView implements RemoteRequestHandler {
    public ClientGameView(GameView underlyingView) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isProcessable(Object input) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Serializable processRequest(Object request) throws NotExecutedException {
        throw new UnsupportedOperationException("Not implemented");
    }
}

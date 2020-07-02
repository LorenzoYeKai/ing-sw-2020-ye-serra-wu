package it.polimi.ingsw.views.game.remote;

import it.polimi.ingsw.requests.RemoteRequestHandler;
import it.polimi.ingsw.views.game.GameView;

import java.io.Serializable;

public class ClientGameView implements RemoteRequestHandler {
    private final GameView underlyingView;

    public ClientGameView(GameView underlyingView) {
        this.underlyingView = underlyingView;
    }

    @Override
    public boolean isProcessable(Object input) {
        return input instanceof Message;
    }

    @Override
    public Serializable processRequest(Object request) {
        ((Message)request).apply(this.underlyingView);
        return null;
    }
}

package it.polimi.ingsw.views.lobby.rpc;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.rpc.RemoteCommandHandler;
import it.polimi.ingsw.views.lobby.LobbyView;

import java.io.Serializable;

public class RemoteLobbyView implements RemoteCommandHandler {
    private final LobbyView view;

    public RemoteLobbyView(LobbyView underlyingView) {
        view = underlyingView;
    }

    @Override
    public boolean isProcessable(Object command) {
        return command instanceof Message || command instanceof GameStartedMessage;
    }

    @Override
    public Serializable processCommand(Object command) throws NotExecutedException {
        assert isProcessable(command);
        if (command instanceof GameStartedMessage) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
        ((Message) command).apply(this.view);
        return null;
    }
}

package it.polimi.ingsw.controller.game.remote;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.requests.RemoteRequestHandler;
import it.polimi.ingsw.requests.RequestProcessor;
import it.polimi.ingsw.views.game.GameView;
import it.polimi.ingsw.views.game.remote.ServerGameView;
import it.polimi.ingsw.views.lobby.LobbyView;
import it.polimi.ingsw.views.lobby.remote.ServerLobbyView;

import java.io.IOException;
import java.io.Serializable;;

public class ServerGameController implements RemoteRequestHandler, AutoCloseable {
    private final RequestProcessor requestProcessor;
    private final GameController controller;

    public ServerGameController(RequestProcessor requestProcessor,
                                GameController underlyingController) {
        this.requestProcessor = requestProcessor;
        this.controller = underlyingController;
    }

    @Override
    public void close() {
        // TODO: if user hasn't left the game when close() is called, very probably
        //  because an exceptional situation, then let's help the player to leave
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isProcessable(Object input) {
        return input instanceof RemoteCommand || input instanceof JoinGameCommand;
    }

    @Override
    public Serializable processRequest(Object request) throws NotExecutedException {
        assert this.isProcessable(request);

        if (request instanceof JoinGameCommand) {
            String nickname = ((JoinGameCommand) request).getNickname();
            GameView view = new ServerGameView(this.requestProcessor);
            try {
                this.controller.joinGame(nickname, view);
            } catch (IOException e) {
                // network errors will only happen in these following situations:
                // - the underlying LobbyController is another ClientGameController
                //   (LocalLobbyController will not throw IOException)
                // So for now we can just assume IOException will never occur here.
                throw new InternalError("IOException when processing command");
            }
        }
        else {
            RemoteCommand command = (RemoteCommand)request;
            try {
                command.apply(this.controller);
            } catch (IOException e) {
                // network errors will only happen in these following situations:
                // - the underlying LobbyController is another ClientGameController
                //   (LocalLobbyController will not throw IOException)
                // So for now we can just assume IOException will never occur here.
                throw new InternalError("IOException when processing command");
            }
        }

        return null;
    }


}

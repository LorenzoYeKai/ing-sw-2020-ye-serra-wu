package it.polimi.ingsw.views.game.remote;

import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.requests.RequestProcessor;
import it.polimi.ingsw.views.game.GameView;

import java.util.Collection;

public class ServerGameView implements GameView {
    private final RequestProcessor connection;

    public ServerGameView(RequestProcessor connection) {
        this.connection = connection;
    }

    @Override
    public void notifyGameStatus(GameStatus status) {
        this.connection.remoteNotify(new GameStatusMessage(status));
    }

    @Override
    public void notifyAvailableGods(Collection<GodType> availableGods) {
        this.connection.remoteNotify(new AvailableGodsMessage(availableGods));
    }

    @Override
    public void notifyPlayerHasGod(String player, GodType playerGod) {
        this.connection.remoteNotify(new PlayerHasGodMessage(player, playerGod));
    }

    @Override
    public void notifySpaceChange(Space space) {
        this.connection.remoteNotify(new SpaceChangedMessage(space));
    }

    @Override
    public void notifyPlayerTurn(String player) {
        this.connection.remoteNotify(new PlayerTurnMessage(player));
    }

    @Override
    public void notifyPlayerDefeat(String player) {
        this.connection.remoteNotify(new PlayerDefeatedMessage(player));
    }
}

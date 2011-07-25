package it.polimi.ingsw.views.game;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.Space;

public abstract class GameView {

    protected final GameController controller;

    protected GameView(GameController controller) {
        this.controller = controller;
    }

    public abstract void notifyGameStatus(GameStatus status);
    public abstract void notifySpaceChange(Space space);
    public abstract void notifyPlayerTurn(String player);
    public abstract void notifyPlayerDefeat(String player);

}

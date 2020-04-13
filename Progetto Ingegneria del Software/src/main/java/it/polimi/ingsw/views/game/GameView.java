package it.polimi.ingsw.views.game;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.PlayerData;
import it.polimi.ingsw.models.game.SpaceData;

public abstract class GameView {

    protected final GameController controller;

    protected GameView(GameController controller) {
        this.controller = controller;
    }

    public abstract void notifyGameStatus(GameStatus status);
    public abstract void notifySpaceChange(SpaceData spaceData);
    public abstract void notifyPlayerTurn(PlayerData player);
    public abstract void notifyPlayerDefeat(PlayerData player);

}

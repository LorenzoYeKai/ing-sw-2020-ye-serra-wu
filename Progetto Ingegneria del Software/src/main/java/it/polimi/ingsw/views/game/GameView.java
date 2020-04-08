package it.polimi.ingsw.views.game;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.PlayerData;
import it.polimi.ingsw.models.game.SpaceData;
import it.polimi.ingsw.models.game.WorkerData;

public abstract class GameView {
    protected final GameController controller;

    protected GameView(GameController controller) {
        this.controller = controller;
    }

    public abstract void notifySpaceChange(SpaceData spaceData);
    public abstract void notifyPlayerLose(PlayerData player);
    public abstract void notifyWorkerActive(WorkerData worker);
}

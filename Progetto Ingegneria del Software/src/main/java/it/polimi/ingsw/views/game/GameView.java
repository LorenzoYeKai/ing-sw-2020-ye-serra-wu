package it.polimi.ingsw.views.game;

import it.polimi.ingsw.controller.game.GameController;

public abstract class GameView {
    protected final GameController controller;

    protected GameView(GameController controller) {
        this.controller = controller;
    }
}

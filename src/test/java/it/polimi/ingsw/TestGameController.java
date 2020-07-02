package it.polimi.ingsw;

import it.polimi.ingsw.controller.game.LocalGameController;
import it.polimi.ingsw.models.game.Game;

import java.util.List;

/**
 * A LocalGameController suitable for testing,
 * because it exposes the {@link Game}, which can be convenient.
 */
public class TestGameController extends LocalGameController {

    public TestGameController(List<String> nicknames) {
        super(nicknames);
    }

    public Game getGame() {
        return this.game;
    }
}
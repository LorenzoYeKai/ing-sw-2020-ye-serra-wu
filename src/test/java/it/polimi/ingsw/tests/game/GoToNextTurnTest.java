package it.polimi.ingsw.tests.game;

import it.polimi.ingsw.models.game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class GoToNextTurnTest {
    Game game;

    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2", "player 3");
        game = new Game(names);

        game.setCurrentPlayer(0);
        game.getCurrentPlayer().selectWorker(0);
    }

    @Test
    @DisplayName("Test go to next turn")
    public void goNextTurnTest (){
        game.setCurrentPlayer(0);
        game.goToNextTurn();
    }
}

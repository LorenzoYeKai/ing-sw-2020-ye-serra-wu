package it.polimi.ingsw.tests.game;

import it.polimi.ingsw.models.game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class NextPlayerTest {
    Game game;

    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2", "player 3");
        game = new Game(names);
        game.setCurrentPlayer(0);
    }

    @Test
    @DisplayName("Test turn change")
    public void nextPlayerTest() {

        game.goToNextTurn();
        assertEquals(1, game.getCurrentPlayerIndex());
        assertNotEquals(2, game.getCurrentPlayerIndex());
        assertNotEquals(0, game.getCurrentPlayerIndex());

        game.goToNextTurn();

        assertNotEquals(1, game.getCurrentPlayerIndex());
        assertNotEquals(0, game.getCurrentPlayerIndex());
        assertEquals(2, game.getCurrentPlayerIndex());

        game.goToNextTurn();
        assertEquals(0, game.getCurrentPlayerIndex());
        assertNotEquals(2, game.getCurrentPlayerIndex());
        assertNotEquals(1, game.getCurrentPlayerIndex());
    }
}

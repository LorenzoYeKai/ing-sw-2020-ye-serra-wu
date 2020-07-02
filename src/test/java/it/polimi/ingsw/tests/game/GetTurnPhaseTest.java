package it.polimi.ingsw.tests.game;

import it.polimi.ingsw.models.game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GetTurnPhaseTest {

    private Game game;

    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2", "player 3");
        game = new Game(names);
        game.setCurrentPlayer(0);
    }

    @Test
    @DisplayName("Test get turn phase")
    public void getTurnPhaseTest() {
        assertEquals(0, game.getTurnPhase());
        game.getWorld().update();
        assertEquals(1, game.getTurnPhase());
        game.getWorld().update();
        assertEquals(2, game.getTurnPhase());
        game.clearPreviousWorlds();
        assertEquals(0, game.getTurnPhase());
    }

}

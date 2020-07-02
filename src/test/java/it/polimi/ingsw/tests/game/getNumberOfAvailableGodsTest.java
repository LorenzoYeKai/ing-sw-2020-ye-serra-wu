package it.polimi.ingsw.tests.game;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class getNumberOfAvailableGodsTest {

    private Game game;

    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2", "player 3");
        game = new Game(names);

    }

    @Test
    @DisplayName("Test number of available gods")
    public void getNumberOfAvailableGodTest() {
        game.addAvailableGods(GodType.APOLLO);
        game.addAvailableGods(GodType.DEMETER);
        game.addAvailableGods(GodType.ATLAS);
        assertEquals(game.getNumberOfAvailableGods(), game.getListOfPlayers().size());
    }
}

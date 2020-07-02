package it.polimi.ingsw.tests.game;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


import java.util.List;

public class IsGodAvailableTest {
    private Game game;

    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2", "player 3");
        game = new Game(names);
        game.addAvailableGods(GodType.APOLLO);
        game.addAvailableGods(GodType.ARTEMIS);
        game.addAvailableGods(GodType.ATHENA);
    }

    @Test
    @DisplayName("Check list of available gods")
    public void isGodAvailableTest() {

        assertTrue(game.getAvailableGods().contains(GodType.APOLLO));
        assertTrue(game.getAvailableGods().contains(GodType.ARTEMIS));
        assertTrue(game.getAvailableGods().contains(GodType.ATHENA));
        assertFalse(game.getAvailableGods().contains(GodType.ATLAS));
        assertFalse(game.getAvailableGods().contains(GodType.DEMETER));
        assertFalse(game.getAvailableGods().contains(GodType.HEPHAESTUS));
        assertFalse(game.getAvailableGods().contains(GodType.MINOTAUR));
        assertFalse(game.getAvailableGods().contains(GodType.PAN));
        assertFalse(game.getAvailableGods().contains(GodType.PROMETHEUS));

    }

}

package it.polimi.ingsw.GameTest;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class addAvaiableGodsTest {
    Game game;
    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);

    }
    @Test
    @DisplayName("Aggiunta Potere e rimozione Potere")
    void addAvaiableGodsTest (){
        game.addAvailableGods(GodType.APOLLO);
        assertTrue(game.getAvailableGods().contains(GodType.APOLLO));
        game.removeAvailableGod(GodType.APOLLO);
        assertFalse(game.getAvailableGods().contains(GodType.APOLLO));
        game.addAvailableGods(GodType.ARTEMIS);
        assertTrue(game.getAvailableGods().contains(GodType.ARTEMIS));
        game.removeAvailableGod(GodType.ARTEMIS);
        assertFalse(game.getAvailableGods().contains(GodType.ARTEMIS));
        game.addAvailableGods(GodType.ATHENA);
        assertTrue(game.getAvailableGods().contains(GodType.ATHENA));
        game.removeAvailableGod(GodType.ATHENA);
        assertFalse(game.getAvailableGods().contains(GodType.ATHENA));
        game.addAvailableGods(GodType.ATLAS);
        assertTrue(game.getAvailableGods().contains(GodType.ATLAS));
        game.removeAvailableGod(GodType.ATLAS);
        assertFalse(game.getAvailableGods().contains(GodType.ATLAS));
        game.addAvailableGods(GodType.DEMETER);
        assertTrue(game.getAvailableGods().contains(GodType.DEMETER));
        game.removeAvailableGod(GodType.DEMETER);
        assertFalse(game.getAvailableGods().contains(GodType.DEMETER));
        game.addAvailableGods(GodType.HEPHAESTUS);
        assertTrue(game.getAvailableGods().contains(GodType.HEPHAESTUS));
        game.removeAvailableGod(GodType.HEPHAESTUS);
        assertFalse(game.getAvailableGods().contains(GodType.HEPHAESTUS));
        game.addAvailableGods(GodType.MINOTAUR);
        assertTrue(game.getAvailableGods().contains(GodType.MINOTAUR));
        game.removeAvailableGod(GodType.MINOTAUR);
        assertFalse(game.getAvailableGods().contains(GodType.MINOTAUR));
        game.addAvailableGods(GodType.PAN);
        assertTrue(game.getAvailableGods().contains(GodType.PAN));
        game.removeAvailableGod(GodType.PAN);
        assertFalse(game.getAvailableGods().contains(GodType.PAN));
        game.addAvailableGods(GodType.PROMETHEUS);
        assertTrue(game.getAvailableGods().contains(GodType.PROMETHEUS));
        game.removeAvailableGod(GodType.PROMETHEUS);
        assertFalse(game.getAvailableGods().contains(GodType.PROMETHEUS));



    }

}

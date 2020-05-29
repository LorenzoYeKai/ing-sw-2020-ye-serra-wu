package it.polimi.ingsw.GameTest;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class avaiableGodsContains {
    Game game;
    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2","player 3");
        game = new Game(names);
        game.addAvailableGods(GodType.APOLLO);
        game.addAvailableGods(GodType.ARTEMIS);
        game.addAvailableGods(GodType.ATHENA);

    }
    @Test
    @DisplayName("Controllo lista")
    void avaiableGodsContains (){
        List<GodType> testin = List.of(GodType.APOLLO,GodType.ARTEMIS,GodType.ATHENA);
        List<GodType> testout = List.of(GodType.ATLAS,GodType.DEMETER,GodType.HEPHAESTUS,GodType.MINOTAUR,GodType.PAN,GodType.PROMETHEUS);

            assertTrue(game.getAvailableGods().containsAll(testin));
            assertFalse(game.getAvailableGods().containsAll(testout));
    }
}

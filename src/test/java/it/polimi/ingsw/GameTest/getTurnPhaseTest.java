package it.polimi.ingsw.GameTest;

import it.polimi.ingsw.models.game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class getTurnPhaseTest {

    Game game;
    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2","player 3");
        game = new Game(names);
        game.setCurrentPlayer(0);
    }

    @Test
    @DisplayName("getter del fase")
    void getTurnPhaseTest(){
        assertTrue(game.getTurnPhase()==0);
        game.savePreviousWorld();
        assertTrue(game.getTurnPhase()==1);
        game.savePreviousWorld();
        assertTrue(game.getTurnPhase()==2);
        game.clearPreviousWorlds();
        assertTrue(game.getTurnPhase()==0);


    }

}

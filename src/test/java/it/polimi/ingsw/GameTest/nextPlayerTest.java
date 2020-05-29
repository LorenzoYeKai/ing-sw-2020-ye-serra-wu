package it.polimi.ingsw.GameTest;

import it.polimi.ingsw.models.game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class nextPlayerTest {
    Game game;
    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2","player 3");
        game = new Game(names);
        game.setCurrentPlayer(0);
    }

    @Test
    @DisplayName("successione giocatori")
    void nextPlayerTest (){

        game.nextPlayer();
        assertTrue(game.getCurrentPlayerIndex() == 1);
        assertFalse(game.getCurrentPlayerIndex() == 2);
        assertFalse(game.getCurrentPlayerIndex() == 0);

        game.goToNextTurn();

        assertFalse(game.getCurrentPlayerIndex() == 1);
        assertFalse(game.getCurrentPlayerIndex() == 0);
        assertTrue(game.getCurrentPlayerIndex() == 2);

        game.goToNextTurn();
        assertTrue(game.getCurrentPlayerIndex() == 0);
        assertFalse(game.getCurrentPlayerIndex ()== 2);
        assertFalse(game.getCurrentPlayerIndex() == 1);


    }
}

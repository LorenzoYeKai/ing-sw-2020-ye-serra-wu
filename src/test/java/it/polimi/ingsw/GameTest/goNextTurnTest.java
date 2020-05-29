package it.polimi.ingsw.GameTest;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class goNextTurnTest {
    Game game;

    @BeforeEach
    void init() {
        List<String> names = List.of("player 1", "player 2", "player 3");
        game = new Game(names);
        Player player1 = new Player(game,"player 1");
        Player player2 = new Player(game,"player 2");
        Player player3 = new Player(game,"player 3");

        game.setCurrentPlayer(0);
    }

    @Test
    @DisplayName("Test successione turni")
    void goNextTurnTest (){
        game.setCurrentPlayer(0);
        game.goToNextTurn();

    }
}

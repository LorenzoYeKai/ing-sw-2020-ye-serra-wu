package it.polimi.ingsw.PlayerTest;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class getSelectWorkerTest {

    Game game;
    Player player;

    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2","player 3");

        game = new Game(names);
        player = new Player (game,"player 1");
        player.getAllWorkers().get(0).setStartPosition(game.getWorld().get(0,0));
        player.getAllWorkers().get(1).setStartPosition(game.getWorld().get(1,1));

    }
    @Test
    @DisplayName("successione giocatori")
    void getSelectWorkerTest(){

    }
}

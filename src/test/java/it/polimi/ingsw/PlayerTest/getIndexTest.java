package it.polimi.ingsw.PlayerTest;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class getIndexTest {

    Game game1,game2;
    Player player,test;

    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2","player 3");
        List<String> names1 = List.of("test", "player 2","player 3");
        game1 = new Game(names);
        game2 = new Game(names1);
        player = new Player (game1,"player 1");
        player.setGod(GodType.APOLLO);
        test = new Player (game2,"test");
    }
    @Test
    @DisplayName("successione giocatori")
    void getIndexTest(){
        int i = player.getIndex();
        int k = test.getIndex();
        assertTrue(player.getName().equals(game1.getListOfPlayers().get(i).getName()));
        assertFalse(player.getName().equals(game2.getListOfPlayers().get(i).getName()));
    }


}

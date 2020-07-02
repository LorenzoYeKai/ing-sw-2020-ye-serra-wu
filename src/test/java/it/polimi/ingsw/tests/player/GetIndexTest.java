package it.polimi.ingsw.tests.player;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GetIndexTest {

    private Game game1, game2;
    private Player player, test;

    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2", "player 3");
        List<String> names1 = List.of("test", "player 2", "player 3");
        game1 = new Game(names);
        game2 = new Game(names1);
        player = new Player(game1, "player 1");
        player.setGod(new GodFactory().getGod(GodType.APOLLO));
        test = new Player(game2, "test");
    }

    @Test
    @DisplayName("Test player list index")
    public void getIndexTest() {
        int i = player.getIndex();
        int k = test.getIndex();
        assertEquals(player.getName(), game1.getListOfPlayers().get(i).getName());
        assertNotEquals(player.getName(), game2.getListOfPlayers().get(i).getName());
    }


}

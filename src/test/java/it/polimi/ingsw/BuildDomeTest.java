package it.polimi.ingsw;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuildDomeTest {

    private Game game;
    private Player player1;


    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
        spaceSetup();
        Space firstWorkerPosition = game.getWorld().get(1, 1);
        Space secondWorkerPosition = game.getWorld().get(2, 2);
        player1.getAllWorkers().get(0).setStartPosition(firstWorkerPosition);
        player1.getAllWorkers().get(1).setStartPosition(secondWorkerPosition);
    }

    @Test
    @DisplayName("Dome construction tests")
    public void buildDomeTest() {
        assertFalse(game.getWorld().get(2, 1).isOccupiedByDome());
        player1.getAllWorkers().get(0).buildDome(game.getWorld().get(2, 1));
        assertTrue(game.getWorld().get(2, 1).isOccupiedByDome());
    }

    private void spaceSetup() {
        World world = game.getWorld();
        world.update(world.get(1, 1).addLevel());//[1][1] level 1
        for (int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); //[2][1] level 3
        for (int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); //[2][2] level 2
        for (int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); //[1][2] level 3 with dome
        world.update(world.get(1, 2).setDome());
    }

    @Test
    @DisplayName("rimozione pedina dopo sconfitta")
    void removeWOrkerWhenDefeat() {
        assertTrue(game.getWorld().get(1, 1).isOccupiedByWorker());
        assertTrue(game.getWorld().get(2, 2).isOccupiedByWorker());
        player1.getAllWorkers().get(1).removeWorkerWhenDefeated();
        player1.getAllWorkers().get(0).removeWorkerWhenDefeated();
        assertFalse(game.getWorld().get(1, 1).isOccupiedByWorker());
        assertFalse(game.getWorld().get(2, 2).isOccupiedByWorker());
    }

    @Test
    @DisplayName("Test getIndex")
    void getIndexTest() {
        assertEquals(player1.getName(), game.getListOfPlayers().get(player1.getIndex()).getName());
    }
}

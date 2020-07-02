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

public class buildDomeTest {

    Game game;
    Player player1;


    @BeforeEach
    void init() {
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
        spaceSetup();
        Space firstWorkerPosition = game.getWorld().getSpaces(1, 1);
        Space secondWorkerPosition = game.getWorld().getSpaces(2, 2);
        player1.getAllWorkers().get(0).setStartPosition(firstWorkerPosition);
        player1.getAllWorkers().get(1).setStartPosition(secondWorkerPosition);
    }

    @Test
    @DisplayName("Simple BuildDome Test")
    void buildDomeTest() {
        assertFalse(game.getWorld().getSpaces(2, 1).isOccupiedByDome());
        player1.getAllWorkers().get(0).buildDome(game.getWorld().getSpaces(2, 1));
        assertTrue(game.getWorld().getSpaces(2, 1).isOccupiedByDome());
    }

    void spaceSetup() {
        World world = game.getWorld();
        world.getSpaces(1, 1).addLevel();//[1][1] level 1
        for (int i = 0; i < 3; i++) world.getSpaces(2, 1).addLevel(); //[2][1] level 3
        for (int i = 0; i < 2; i++) world.getSpaces(2, 2).addLevel(); //[2][2] level 2
        for (int i = 0; i < 3; i++) world.getSpaces(1, 2).addLevel(); //[1][2] level 3 with dome
        world.getSpaces(1, 2).setDome();
    }

    @Test
    @DisplayName("test posizione iniziale")
    void getInitialSpaceTest() {
        Space firstWorkerPosition = game.getWorld().getSpaces(1, 1);
        player1.getAllWorkers().get(0).move(game.getWorld().getSpaces(0, 0));
        assertEquals(player1.getAllWorkers().get(0).getInitialSpace(), firstWorkerPosition);

    }

    @Test
    @DisplayName("rimozione pedina dopo sconfitta")
    void removeWorkerWhenDefeat() {
        assertTrue(game.getWorld().getSpaces(1, 1).isOccupiedByWorker());
        assertTrue(game.getWorld().getSpaces(2, 2).isOccupiedByWorker());
        player1.getAllWorkers().get(1).removeWorkerWhenDefeated();
        player1.getAllWorkers().get(0).removeWorkerWhenDefeated();
        assertFalse(game.getWorld().getSpaces(1, 1).isOccupiedByWorker());
        assertFalse(game.getWorld().getSpaces(2, 2).isOccupiedByWorker());
    }

    @Test
    @DisplayName("Test getIndex")
    void getIndexTest() {
        assertTrue(player1.getName().equals(game.getListOfPlayers().get(player1.getIndex()).getName()));
    }
}

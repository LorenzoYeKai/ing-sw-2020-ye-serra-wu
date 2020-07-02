package it.polimi.ingsw;

import it.polimi.ingsw.models.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SavePreviousWorldTest {

    Game game;
    Player player1;

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
    @DisplayName("Test the saved previous World after move")
    public void savePreviousWorldMoveTest() {
        player1.getAllWorkers().get(0).move(game.getWorld().get(1, 0));

        assertTrue(player1.getAllWorkers().get(0).isLastActionMove());
        assertFalse(player1.getAllWorkers().get(1).isLastActionMove());

        player1.getAllWorkers().get(0).move(game.getWorld().get(0, 0));
        assertTrue(player1.getAllWorkers().get(0).isLastActionMove());
        assertFalse(player1.getAllWorkers().get(1).isLastActionMove());

        player1.getAllWorkers().get(0).buildBlock(game.getWorld().get(0, 1));
        assertFalse(player1.getAllWorkers().get(0).isLastActionMove());
        assertTrue(player1.getAllWorkers().get(0).getPreviouslyBuiltBlock().isPresent());
    }

    @Test
    @DisplayName("Test the saved previous World after Build")
    public void savePreviousWorldBuildBlockTest() {
        player1.getAllWorkers().get(0).buildBlock(game.getWorld().get(1, 0));
        assertTrue(player1.getAllWorkers().get(0).getPreviouslyBuiltBlock().isPresent());
    }

    private void spaceSetup() {
        World world = game.getWorld();
        world.update(world.get(1, 1).addLevel()); // [1][1] level 1
        for (int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); //[2][1] level 3
        for (int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); //[2][2] level 2
        for (int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); //[1][2] level 3 with dome
        world.update(world.get(1, 2).setDome());
    }

}

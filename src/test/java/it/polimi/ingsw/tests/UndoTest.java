package it.polimi.ingsw.tests;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.models.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UndoTest {

    private Game game;
    private TestGameController controller;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2");
        controller = new TestGameController(names);
        game = controller.getGame();
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
        player2 = game.findPlayerByName("player 1");
        spaceSetup();
        Space firstWorkerPosition = game.getWorld().get(1, 1);
        Space secondWorkerPosition = game.getWorld().get(2, 2);
        player1.getAllWorkers().get(0).setStartPosition(firstWorkerPosition);
        player1.getAllWorkers().get(1).setStartPosition(secondWorkerPosition);
        Space player2FirstWorkerPosition = game.getWorld().get(2, 0);
        Space player2SecondWorkerPosition = game.getWorld().get(3, 2);
        player2.getAllWorkers().get(0).setStartPosition(player2FirstWorkerPosition);
        player2.getAllWorkers().get(1).setStartPosition(player2SecondWorkerPosition);
        game.clearPreviousWorlds();
    }

    @Test
    @DisplayName("Undo with a move:")
    public void undoMoveTest() throws NotExecutedException {

        //Worker moves

        player1.getAllWorkers().get(0).move(game.getWorld().get(1, 0));

        assertTrue(player1.getAllWorkers().get(0).isLastActionMove());
        assertFalse(player1.getAllWorkers().get(1).isLastActionMove());

        //UNDO after 1 move, the previousWorld list should be empty after undo

        controller.undo();

        assertThrows(UnsupportedOperationException.class, () -> game.getPreviousWorld());

        for (Player p : game.getListOfPlayers()) {
            p.getAllWorkers().forEach(w -> assertTrue(getWorkersInWorld(game.getWorld()).contains(w.getIdentity())));
        }
    }

    @Test
    @DisplayName("Undo with a move and a build:")
    public void undoMoveAndBuildTest() throws NotExecutedException {

        //Worker moves

        player1.getAllWorkers().get(0).move(game.getWorld().get(1, 0));

        assertTrue(player1.getAllWorkers().get(0).isLastActionMove());
        if (player1.getAllWorkers().get(0).isLastActionMove()) {
            System.out.println("Worker 0 has moved!");
        }

        assertFalse(player1.getAllWorkers().get(1).isLastActionMove());
        if (!player1.getAllWorkers().get(1).isLastActionMove()) {
            System.out.println("Worker 1 NOT has moved!");
        }

        //Worker builds

        player1.getAllWorkers().get(0).buildBlock(game.getWorld().get(1, 1));
        assertTrue(player1.getAllWorkers().get(0).getPreviouslyBuiltBlock().isPresent());

        //UNDO after 1 move and 1 build
        controller.undo();
        assertFalse(player1.getAllWorkers().get(0).getPreviouslyBuiltBlock().isPresent());

        for (Player p : game.getListOfPlayers()) {
            p.getAllWorkers().forEach(w -> assertTrue(getWorkersInWorld(game.getWorld()).contains(w.getIdentity())));
        }
    }


    private void spaceSetup() {
        World world = game.getWorld();
        world.get(1, 1).addLevel();//[1][1] level 1
        for (int i = 0; i < 3; i++) world.get(2, 1).addLevel(); //[2][1] level 3
        for (int i = 0; i < 2; i++) world.get(2, 2).addLevel(); //[2][2] level 2
        for (int i = 0; i < 3; i++) world.get(1, 2).addLevel(); //[1][2] level 3 with dome
        world.get(1, 2).setDome();
    }

    private List<WorkerData> getWorkersInWorld(World world) {
        List<WorkerData> listOfWorkers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Space space = world.get(i, j);
                if (space.isOccupiedByWorker()) {
                    listOfWorkers.add(space.getWorkerData());
                }
            }
        }
        return listOfWorkers;
    }
}

package it.polimi.ingsw.tests.player;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.WorkerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GetSelectedWorkerTest {

    Game game;
    Player player;

    @BeforeEach
    public void init(){
        List<String> names = List.of("player 1", "player 2","player 3");

        game = new Game(names);
        player = new Player(game,"player 1");
        player.getAllWorkers().get(0).setStartPosition(game.getWorld().get(0,0));
        player.getAllWorkers().get(1).setStartPosition(game.getWorld().get(1,1));
        WorkerData prova = game.getWorld().get(0,0).getWorkerData();
    }

    @Test
    public void getSelectedWorker(){
        assertNotEquals(true, player.hasSelectedAWorker());
        player.selectWorker(0);
        assertEquals(player.getSelectedWorker(), player.getAllWorkers().get(0));
        player.selectWorker(1);
        assertEquals(player.getSelectedWorker(), player.getAllWorkers().get(1));
        assertTrue(player.hasSelectedAWorker());
        player.selectWorker(0);
        player.deselectWorker();

        WorkerData worker1 = game.getWorld().get(0,0).getWorkerData();
        assertEquals(player.getWorker(worker1), player.getAllWorkers().get(0));
        WorkerData worker2 = game.getWorld().get(1,1).getWorkerData();
        assertEquals(player.getWorker(worker2), player.getAllWorkers().get(1));
    }
}

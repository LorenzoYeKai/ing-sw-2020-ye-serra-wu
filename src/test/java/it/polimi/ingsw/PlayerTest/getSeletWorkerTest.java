package it.polimi.ingsw.PlayerTest;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.WorkerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class getSeletWorkerTest {

    Game game;
    Player player;

    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2","player 3");

        game = new Game(names);
        player = new Player(game,"player 1");
        player.getAllWorkers().get(0).setStartPosition(game.getWorld().getSpaces(0,0));
        player.getAllWorkers().get(1).setStartPosition(game.getWorld().getSpaces(1,1));
        WorkerData prova = game.getWorld().getSpaces(0,0).getWorkerData();
    }

    @Test
    void getSelectWorker(){
        assertFalse(player.hasSelectedAWorker()==true);
        player.selectWorker(0);
        assertTrue(player.getSelectedWorker().equals(player.getAllWorkers().get(0)));
        player.selectWorker(1);
        assertTrue(player.getSelectedWorker().equals(player.getAllWorkers().get(1)));
        assertTrue(player.hasSelectedAWorker()==true);
        player.selectWorker(0);
        player.deselectWorker();
        WorkerData worker1 = game.getWorld().getSpaces(0,0).getWorkerData();
        player.getWorker(worker1).equals(player.getAllWorkers().get(0));
        WorkerData worker2 = game.getWorld().getSpaces(1,1).getWorkerData();
        player.getWorker(worker2).equals(player.getAllWorkers().get(1));
    }
}

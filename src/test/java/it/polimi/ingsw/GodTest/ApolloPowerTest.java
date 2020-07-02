package it.polimi.ingsw.GodTest;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.TestGameController;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApolloPowerTest {

    Game game;
    TestGameController controller;
    Player player1;
    Player player2;

    @BeforeEach
    void init(){
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
        game.getCurrentPlayer().setGod(GodType.APOLLO);
    }

    @Test
    @DisplayName("apollo power test")
    void apolloPowerTest() throws NotExecutedException {
        player1.getGod().activateGodPower(game.getRules());
        assertTrue(game.getWorld().get(3,2).isOccupiedByWorker());
        assertTrue(player1.getAllWorkers().get(1).computeAvailableSpaces().contains(game.getWorld().get(3,2)));
        controller.move(player1.getAllWorkers().get(1),game.getWorld().get(3,2));
        assertTrue(game.getWorld().get(2,2).isOccupiedByWorker());
        player1.getGod().deactivateGodPower(game.getRules());
        assertFalse(player1.getAllWorkers().get(1).computeAvailableSpaces().contains(game.getWorld().get(3,2)));
        player1.getGod().forcePower(player1.getAllWorkers().get(0),game.getWorld().get(2,0));
        assertTrue(player1.getAvailableWorkers().get(0).getCurrentSpace().equals(game.getWorld().get(2,0)));


    }
    void spaceSetup(){
        World world = game.getWorld();
        world.update(world.get(1, 1).addLevel());//[1][1] level 1
        for(int i = 0; i < 3; i++) world.update(world.get(2,1).addLevel()); //[2][1] level 3
        for(int i = 0; i < 2; i++) world.update(world.get(2,2).addLevel()); //[2][2] level 2
        for(int i = 0; i < 3; i++) world.update(world.get(1,2).addLevel()); //[1][2] level 3 with dome
        world.update(world.get(1, 2).setDome());
    }
}

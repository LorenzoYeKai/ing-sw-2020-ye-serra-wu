package it.polimi.ingsw.tests;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.DefaultRule;
import it.polimi.ingsw.tests.GameTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApolloTest extends GameTestBase {

    private Game game;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void init() {
        List<String> names = List.of("X", "Y");
        game = new Game(names);
        game.findPlayerByName("Y").setGod(new GodFactory().getGod(GodType.APOLLO));
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
        spaceSetup();
        Space player1FirstWorkerPosition = game.getWorld().get(1, 1);
        Space player1SecondWorkerPosition = game.getWorld().get(2, 2);
        player1.getAllWorkers().get(0).setStartPosition(player1FirstWorkerPosition);
        player1.getAllWorkers().get(1).setStartPosition(player1SecondWorkerPosition);
        game.getCurrentPlayer().selectWorker(0);
        game.goToNextTurn();
        game.clearPreviousWorlds();
        game.getCurrentPlayer().selectWorker(0);
        game.getCurrentPlayer().getAllWorkers().get(0).setStartPosition(game.getWorld().get(2,3));



    }


    @Test
    @DisplayName("availableSpaces with Apollo")
    public void apolloPowerTest() {
        game.getCurrentPlayer().selectWorker(0);
        game.getCurrentPlayer().getAllWorkers().get(0).move(game.getWorld().get(3,1));
        game.getCurrentPlayer().getAllWorkers().get(0).buildBlock(game.getWorld().get(2,0));
        game.goToNextTurn();
        game.getCurrentPlayer().selectWorker(0);
        game.clearPreviousWorlds();
        assertTrue(game.getCurrentPlayer().getAllWorkers().get(0).computeAvailableSpaces().contains(game.getWorld().get(0,0)));
        game.getCurrentPlayer().selectWorker(1);



    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    @DisplayName("Swap")
    public void swapTest() {
        game.goToNextTurn();
        game.clearPreviousWorlds();
        game.getCurrentPlayer().selectWorker(1);
        assertTrue(game.getCurrentPlayer().getAllWorkers().get(1).computeAvailableSpaces().contains(game.getWorld().get(2,3)));
        game.getCurrentPlayer().getAllWorkers().get(1).move(game.getWorld().get(2,3));

    }

    private void spaceSetup() {
        World world = game.getWorld();
        world.update(world.get(1, 1).addLevel());//[1][1] level 1
        for (int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); //[2][1] level 3
        for (int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); //[2][2] level 2
        for (int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); //[1][2] level 3 with dome
        world.update(world.get(1, 2).setDome());;
    }


}

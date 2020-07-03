package it.polimi.ingsw.tests;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.Athena;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AthenaTest {

    private Game game;
    private Player player1,player2;

    @BeforeEach
    public void init(){
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);
        game.findPlayerByName("player 2").setGod(new GodFactory().getGod(GodType.ATHENA));
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
        player2 = game.getListOfPlayers().get(0);
        spaceSetup();
        Space firstWorkerPosition = game.getWorld().get(1, 0);
        Space secondWorkerPosition = game.getWorld().get(2, 2);
        player1.getAllWorkers().get(0).setStartPosition(firstWorkerPosition);
        player1.getAllWorkers().get(1).setStartPosition(secondWorkerPosition);
         firstWorkerPosition = game.getWorld().get(0, 1);
         secondWorkerPosition = game.getWorld().get(3, 3);
        player2.getAllWorkers().get(0).setStartPosition(firstWorkerPosition);
        player2.getAllWorkers().get(1).setStartPosition(firstWorkerPosition);
        game.getCurrentPlayer().selectWorker(0);
        game.getCurrentPlayer().getAllWorkers().get(0).move(game.getWorld().get(0,0));
        game.getCurrentPlayer().getAllWorkers().get(0).buildBlock(game.getWorld().get(1,1));
        game.setCurrentPlayer(0);
    }



    @Test
    @DisplayName("availableSpaces with athenaPower")
    public void athenaPowerTest(){
        game.getCurrentPlayer().selectWorker(0);
        assertFalse(game.getCurrentPlayer().getAllWorkers().get(0).computeAvailableSpaces().contains(game.getWorld().get(1,0)));

    }



    void spaceSetup(){
        World world = game.getWorld();
        world.update(world.get(1, 1).addLevel()); // [1][1] level 1
        for(int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); // [2][1] level 3
        for(int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); // [2][2] level 2
        for(int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); // [1][2] level 3 with dome
        world.update(world.get(1, 2).setDome());
    }


}

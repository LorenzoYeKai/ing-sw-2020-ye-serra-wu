package it.polimi.ingsw.GodTest;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.TestGameController;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.models.game.rules.GodPower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PanPowerTest {
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
        game.getCurrentPlayer().setGod(GodType.PAN);
    }

    @Test
    @DisplayName("pan power test")
    void panPowerTest() throws NotExecutedException {

        game.getCurrentPlayer().getGod().activateGodPower(game.getRules());
        controller.move(game.getCurrentPlayer().getAllWorkers().get(1),game.getWorld().get(2,3));
        assertFalse(game.getCurrentPlayer().isDefeated());
        game.getCurrentPlayer().getGod().deactivateGodPower(game.getRules());
        game.getCurrentPlayer().getGod().deactivateGodPower(game.getRules());




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

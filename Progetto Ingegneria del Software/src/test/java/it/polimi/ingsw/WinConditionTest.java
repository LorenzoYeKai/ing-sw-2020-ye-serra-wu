package it.polimi.ingsw;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.rules.GodPower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WinConditionTest {


    Game game;
    Player player1;

    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);
        game.setCurrentTurn(1);
        player1 = game.getCurrentPlayer();
        spaceSetup();
        Space firstWorkerPosition = game.getWorld().getSpaces(1, 1);
        Space secondWorkerPosition = game.getWorld().getSpaces(2, 2);
        player1.getAllWorkers().get(0).setStartPosition(firstWorkerPosition);
        player1.getAllWorkers().get(1).setStartPosition(secondWorkerPosition);
    }

    @Test
    @DisplayName("Win condition without God Powers")
    void DefaultWinConditionTest(){
        assertTrue(game.getRules().winCondition(game.getWorld().getSpaces(2, 2), game.getWorld().getSpaces(2, 1)));
        System.out.println("No power default:");
        player1.getAllWorkers().get(1).move(game.getWorld().getSpaces(2, 1));
    }

    @Test
    @DisplayName("Win condition with Pan's power")
    void PanWinConditionTest(){
        game.getRules().addWinConditions("PanPower", GodPower::panPower);
        assertTrue(game.getRules().winCondition(game.getWorld().getSpaces(2, 2), game.getWorld().getSpaces(2, 1)));
        System.out.println("Pan Power Default:");
        player1.getAllWorkers().get(1).move(game.getWorld().getSpaces(2, 1));
        assertFalse(game.getRules().winCondition(game.getWorld().getSpaces(1, 1), game.getWorld().getSpaces(2, 2)));
        player1.getAllWorkers().get(0).move(game.getWorld().getSpaces(2, 2));
        assertTrue(game.getRules().winCondition(game.getWorld().getSpaces(2, 2), game.getWorld().getSpaces(3, 2)));
        System.out.println("Pan Power Down 2 levels:");
        player1.getAllWorkers().get(0).move(game.getWorld().getSpaces(3, 2));
    }



    void spaceSetup(){
        World world = game.getWorld();
        world.getSpaces(1, 1).addLevel();//[1][1] level 1
        for(int i = 0; i < 3; i++) world.getSpaces(2, 1).addLevel(); //[2][1] level 3
        for(int i = 0; i < 2; i++) world.getSpaces(2, 2).addLevel(); //[2][2] level 2
        for(int i = 0; i < 3; i++) world.getSpaces(1, 2).addLevel(); //[1][2] level 3 with dome
        world.getSpaces(1, 2).setDome();
    }

}

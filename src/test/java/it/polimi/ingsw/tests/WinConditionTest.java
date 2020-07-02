package it.polimi.ingsw.tests;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WinConditionTest {

    private Game game;
    private Player player1;

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
    @DisplayName("Win condition without God Powers")
    public void DefaultWinConditionTest() {
        assertTrue(game.getRules().winCondition(player1.getAllWorkers().get(1), game.getWorld().get(2, 1)));
        player1.getAllWorkers().get(1).move(game.getWorld().get(2, 1));
    }

    @Test
    @DisplayName("Win condition with Pan's power")
    public void PanWinConditionTest() {
        God pan = new GodFactory().getGod(GodType.PAN);
        pan.activateGodPower(game.getRules());
        // normal win condition still applies
        assertTrue(game.getRules().winCondition(player1.getAllWorkers().get(1), game.getWorld().get(2, 1)));

        assertFalse(game.getRules().winCondition(player1.getAllWorkers().get(0), game.getWorld().get(2, 2)));
        player1.getAllWorkers().get(0).move(game.getWorld().get(2, 2));

        // Pan Power Down 2 levels
        assertTrue(game.getRules().winCondition(player1.getAllWorkers().get(0), game.getWorld().get(3, 2)));
        player1.getAllWorkers().get(0).move(game.getWorld().get(3, 2));
    }


    private void spaceSetup() {
        World world = game.getWorld();
        world.update(world.get(1, 1).addLevel()); // [1][1] level 1
        for (int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); // [2][1] level 3
        for (int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); // [2][2] level 2
        for (int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); // [1][2] level 3 with dome
        world.update(world.get(1, 2).setDome());
    }

}

package it.polimi.ingsw.tests;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MinotaurTest {

    private Game game;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);
        game.findPlayerByName("player 2").setGod(new GodFactory().getGod(GodType.MINOTAUR));
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
        player2 = game.findPlayerByName("player 1");
        spaceSetup();
        game.getCurrentPlayer().selectWorker(0);
        game.getCurrentPlayer().getAllWorkers().get(0).setStartPosition(game.getWorld().get(2,3));
        game.goToNextTurn();
        game.clearPreviousWorlds();
        game.getCurrentPlayer().selectWorker(0);
        game.getCurrentPlayer().getAllWorkers().get(0).setStartPosition(game.getWorld().get(3,3));
        game.goToNextTurn();
        game.clearPreviousWorlds();

    }

    @Test
    @DisplayName("availableSpaces without god powers")
    public void computeAvailableSpacesTest() {
        game.getCurrentPlayer().selectWorker(0);
        assertTrue(game.getCurrentPlayer().getAllWorkers().get(0).computeAvailableSpaces().contains(game.getWorld().get(1,3)));
    }

    @Test
    @DisplayName("availableSpaces with Minotaur")
    public void minotaurPowerTest() {
        game.getCurrentPlayer().selectWorker(0);
        assertTrue(game.getCurrentPlayer().getAllWorkers().get(0).computeAvailableSpaces().contains(game.getWorld().get(3,3)));
        game.getCurrentPlayer().getAllWorkers().get(0).move(game.getWorld().get(3,3));
    }



    private void asserting(List<Space> expected1, List<Space> expected2,
                           List<Space> actual1, List<Space> actual2) {
        expected1.forEach(space -> assertTrue(actual1.contains(space)));
        actual1.forEach((space -> assertTrue(expected1.contains(space))));
        expected2.forEach(space -> assertTrue(actual2.contains(space)));
        actual2.forEach((space -> assertTrue(expected2.contains(space))));

    }

    private void spaceSetup() {
        World world = game.getWorld();
        world.update(world.get(1, 1).addLevel()); // [1][1] level 1
        for (int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); // [2][1] level 3
        for (int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); // [2][2] level 2
        for (int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); // [1][2] level 3 with dome
        world.update(world.get(1, 2).setDome());
    }

    private List<Space> manualAvailableSpaces1() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(0, 0));
        availableSpaces.add(world.get(0, 1));
        availableSpaces.add(world.get(0, 2));
        availableSpaces.add(world.get(1, 0));

        return availableSpaces;
    }

    private List<Space> manualAvailableSpaces2() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(3, 3));

        availableSpaces.add(world.get(3, 1));
        availableSpaces.add(world.get(1, 3));
        availableSpaces.add(world.get(2, 3));
        availableSpaces.add(world.get(2, 1));
        return availableSpaces;
    }

    private List<Space> manualMinotaurAvailableSpaces1() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(0, 0));
        availableSpaces.add(world.get(0, 1));
        availableSpaces.add(world.get(0, 2));
        availableSpaces.add(world.get(1, 0));

        return availableSpaces;
    }

    private List<Space> manualMinotaurAvailableSpaces2() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(3, 3));
        availableSpaces.add(world.get(3, 2));
        availableSpaces.add(world.get(3, 1));
        availableSpaces.add(world.get(1, 3));
        availableSpaces.add(world.get(2, 3));
        availableSpaces.add(world.get(2, 1));
        return availableSpaces;
    }
}

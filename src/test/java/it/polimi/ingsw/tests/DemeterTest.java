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

public class DemeterTest {
    Game game;
    Player player1;

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
    @DisplayName("buildableSpaces without god powers")
    public void computeBuildableSpacesTest() {
        List<Space> expected1 = manualAvailableSpaces1();
        List<Space> expected2 = manualAvailableSpaces2();
        List<Space> actual1 = player1.getAllWorkers().get(0).computeBuildableSpaces();
        List<Space> actual2 = player1.getAllWorkers().get(1).computeBuildableSpaces();
        asserting(expected1, expected2, actual1, actual2);
    }

    @Test
    @DisplayName("Second build with Demeter")
    public void demeterPowerTest() {
        player1.getAllWorkers().get(0).buildBlock(game.getWorld().get(1, 0));

        // Worker 1 has built correctly so the level of [1][0] is now 1
        assertEquals(1, game.getWorld().get(1, 0).getLevel());

        God god = new GodFactory().getGod(GodType.DEMETER);
        god.activateGodPower(game.getRules());
        List<Space> actualSpaces = player1.getAllWorkers().get(0).computeBuildableSpaces();
        List<Space> expectedSpaces = demeterAvailableSpaces1();

        expectedSpaces.forEach(space -> assertTrue(actualSpaces.contains(space)));
        actualSpaces.forEach((space -> assertTrue(expectedSpaces.contains(space))));
    }

    @Test
    @DisplayName("BuildableSpaces with Demeter")
    public void buildableSpacesDemeterTest() {
        God god = new GodFactory().getGod(GodType.DEMETER);
        god.activateGodPower(game.getRules());
        player1.getAllWorkers().get(0).move(game.getWorld().get(1, 0));

        List<Space> expected1 = manualAvailableSpaces3();
        List<Space> actual1 = player1.getAllWorkers().get(0).computeBuildableSpaces();

        expected1.forEach(space -> assertTrue(actual1.contains(space)));
        actual1.forEach((space -> assertTrue(expected1.contains(space))));

        player1.getAllWorkers().get(0).buildBlock(game.getWorld().get(2, 0));

        List<Space> expected2 = manualAvailableSpaces4();
        List<Space> actual2 = player1.getAllWorkers().get(0).computeBuildableSpaces();

        expected2.forEach(space -> assertTrue(actual2.contains(space)));
        actual2.forEach((space -> assertTrue(expected2.contains(space))));
    }

    void asserting(List<Space> expected1, List<Space> expected2,
                   List<Space> actual1, List<Space> actual2) {
        expected1.forEach(space -> assertTrue(actual1.contains(space)));
        actual1.forEach((space -> assertTrue(expected1.contains(space))));
        expected2.forEach(space -> assertTrue(actual2.contains(space)));
        actual2.forEach((space -> assertTrue(expected2.contains(space))));
    }

    void spaceSetup() {
        World world = game.getWorld();
        world.update(world.get(1, 1).addLevel()); // [1][1] level 1
        for (int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); // [2][1] level 3
        for (int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); // [2][2] level 2
        for (int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); // [1][2] level 3 with dome
        for (int i = 0; i < 3; i++) world.update(world.get(4, 0).addLevel());
        world.update(world.get(1, 2).setDome());
    }

    List<Space> manualAvailableSpaces1() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(0, 0));
        availableSpaces.add(world.get(0, 1));
        availableSpaces.add(world.get(0, 2));
        availableSpaces.add(world.get(1, 0));
        availableSpaces.add(world.get(2, 0));
        return availableSpaces;
    }

    List<Space> manualAvailableSpaces2() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(3, 3));
        availableSpaces.add(world.get(3, 2));
        availableSpaces.add(world.get(3, 1));
        availableSpaces.add(world.get(1, 3));
        availableSpaces.add(world.get(2, 3));
        return availableSpaces;
    }

    List<Space> demeterAvailableSpaces1() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(0, 0));
        availableSpaces.add(world.get(0, 1));
        availableSpaces.add(world.get(0, 2));
        availableSpaces.add(world.get(2, 0));
        return availableSpaces;
    }

    List<Space> demeterAvailableSpaces2() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(3, 3));
        availableSpaces.add(world.get(3, 2));
        availableSpaces.add(world.get(3, 1));
        availableSpaces.add(world.get(1, 3));
        availableSpaces.add(world.get(2, 3));
        return availableSpaces;
    }

    List<Space> manualAvailableSpaces3() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(0, 0));
        availableSpaces.add(world.get(1, 1));
        availableSpaces.add(world.get(0, 1));
        availableSpaces.add(world.get(2, 0));
        return availableSpaces;
    }

    List<Space> manualAvailableSpaces4() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(0, 0));
        availableSpaces.add(world.get(1, 1));
        availableSpaces.add(world.get(0, 1));
        return availableSpaces;
    }


}

package it.polimi.ingsw.tests;

import it.polimi.ingsw.models.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuildableSpaceTest {

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
    @DisplayName("buildableSpaces without god powers")
    public void computeBuildableSpacesTest() {
        var expected1 = manualAvailableSpaces1();
        var expected2 = manualAvailableSpaces2();
        var actual1 = player1.getAllWorkers().get(0).computeBuildableSpaces();
        var actual2 = player1.getAllWorkers().get(1).computeBuildableSpaces();
        asserting(expected1, expected2, actual1, actual2);
    }

    @Test
    @DisplayName("buildDome without god powers")
    public void buildDomeTest() {
        var expected1 = manualDomeSpaces();
        var expected2 = manualDomeSpaces();
        var actual1 = player1.getAllWorkers().get(0).computeDomeSpaces();
        var actual2 = player1.getAllWorkers().get(1).computeDomeSpaces();
        asserting(expected1, expected2, actual1, actual2);
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
        world.update(world.get(1, 1).addLevel()); //[1][1] level 1
        for (int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); //[2][1] level 3
        for (int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); //[2][2] level 2
        for (int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); //[1][2] level 3 with dome
        for (int i = 0; i < 3; i++) world.update(world.get(4, 0).addLevel());
        world.update(world.get(1, 2).setDome());
    }

    private List<Space> manualAvailableSpaces1() {
        World world = game.getWorld();
        var availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.get(0, 0));
        availableSpaces.add(world.get(0, 1));
        availableSpaces.add(world.get(0, 2));
        availableSpaces.add(world.get(1, 0));
        availableSpaces.add(world.get(2, 0));
        return availableSpaces;
    }

    private List<Space> manualAvailableSpaces2() {
        World world = game.getWorld();
        var availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.get(3, 3));
        availableSpaces.add(world.get(3, 2));
        availableSpaces.add(world.get(3, 1));
        availableSpaces.add(world.get(1, 3));
        availableSpaces.add(world.get(2, 3));
        return availableSpaces;
    }

    private List<Space> manualDomeSpaces() {
        World world = game.getWorld();
        var availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.get(2, 1));
        return availableSpaces;
    }
}

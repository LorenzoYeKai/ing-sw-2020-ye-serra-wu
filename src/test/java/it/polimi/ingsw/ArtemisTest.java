package it.polimi.ingsw;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArtemisTest extends GameTestBase {

    private Game game;
    private Player player1;

    @BeforeEach
    public void init() {
        List<String> names = List.of("~", "I");
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
    @DisplayName("availableSpaces without god powers")
    public void computeAvailableSpacesTest() {
        var expected1 = manualAvailableSpaces1();
        var expected2 = manualAvailableSpaces2();
        var actual1 = player1.getAllWorkers().get(0).computeAvailableSpaces();
        var actual2 = player1.getAllWorkers().get(1).computeAvailableSpaces();
        asserting(expected1, expected2, actual1, actual2);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    @DisplayName("availableSpaces with artemisPower after the first move")
    public void artemisPowerTest() {
        player1.getAllWorkers().get(0).move(game.getWorld().get(2, 0));
        // The previous space was occupied by Worker 1
        Space previousSpace = player1.getAllWorkers().get(0).getPreviousSpace().get();
        assertTrue(previousSpace.isOccupiedByWorker());
        // Worker 1 has moved correctly so the current previous space is free
        assertFalse(game.getWorld().get(previousSpace.getPosition()).isOccupiedByWorker());
        // Worker 1 has moved correctly so the space [2][0] is now occupied by it
        assertTrue(game.getWorld().get(2, 0).isOccupiedByWorker());

        God artemis = new GodFactory().getGod(GodType.ARTEMIS);
        artemis.activateGodPower(game.getRules());

        var expectedSpaces = manualArtemisAvailableSpaces();
        var actualSpaces = player1.getAllWorkers().get(0).computeAvailableSpaces();

        //asserting the actual and the expected spaces are equal
        expectedSpaces.forEach(space -> assertTrue(actualSpaces.contains(space)));
        actualSpaces.forEach((space -> assertTrue(expectedSpaces.contains(space))));
    }

    private void spaceSetup() {
        World world = game.getWorld();
        world.update(world.get(1, 1).addLevel()); //[1][1] level 1
        for (int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); //[2][1] level 3
        for (int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); //[2][2] level 2
        for (int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); //[1][2] level 3 with dome
        world.update(world.get(1, 2).setDome());
    }

    private List<Space> manualAvailableSpaces1() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(0, 0));
        availableSpaces.add(world.get(0, 1));
        availableSpaces.add(world.get(0, 2));
        availableSpaces.add(world.get(1, 0));
        availableSpaces.add(world.get(2, 0));
        return availableSpaces;
    }

    private List<Space> manualAvailableSpaces2() {
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

    private List<Space> manualArtemisAvailableSpaces() {
        World world = game.getWorld();
        List<Space> availableSpaces = new ArrayList<>();
        availableSpaces.add(world.get(1, 0));
        availableSpaces.add(world.get(3, 0));
        availableSpaces.add(world.get(3, 1));
        return availableSpaces;
    }
}

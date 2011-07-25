package it.polimi.ingsw;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.rules.ActualRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class ActualRuleTest {

    private World world;

    @BeforeEach
    public void init() {
        world = new World(value -> {});
    }

    @Test
    @DisplayName("Testing with no god power")
    public void defaultCanMoveThereTest() {
        world.update(world.get(1, 0).addLevel()); // (1, 0) level 2
        world.update(world.get(1, 0).addLevel());
        world.update(world.get(2, 1).addLevel()); // (2, 1) level 1
        world.update(world.get(0, 1).setDome()); // (0, 1) occupied by dome
        ActualRule rules = new ActualRule(world);

        Function<Space, Worker> getWorker = space -> {
            Worker worker = new MockWorker(world, rules);
            worker.setStartPosition(space);
            return worker;
        };
        assertTrue(rules.canMoveThere(getWorker.apply(world.get(1, 1)), world.get(2, 1)));
        assertTrue(rules.canMoveThere(getWorker.apply(world.get(1, 1)), world.get(1, 2)));
        assertFalse(rules.canMoveThere(getWorker.apply(world.get(1, 1)), world.get(1, 4)));
        assertFalse(rules.canMoveThere(getWorker.apply(world.get(1, 1)), world.get(1, 0)));
        assertFalse(rules.canMoveThere(getWorker.apply(world.get(1, 1)), world.get(0, 1)));

        // world.get(1, 1) is becoming different after getting the worker.
        assertFalse(rules.canMoveThere(getWorker.apply(world.get(1, 1)), world.get(1, 1)));
    }
}

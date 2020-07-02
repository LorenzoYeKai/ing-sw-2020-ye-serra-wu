package it.polimi.ingsw;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.rules.DefaultRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultRuleTest {

    Game game;
    Player player1;

    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
    }

    @Test
    public void defaultIsNeighborTest() {
        assertTrue(DefaultRule.defaultIsNeighbor(getMockWorker(1, 1), game.getWorld().get(1, 2)));
        assertFalse(DefaultRule.defaultIsNeighbor(getMockWorker(1, 1), game.getWorld().get(1, 4)));
        assertFalse(DefaultRule.defaultIsNeighbor(getMockWorker(1, 1), game.getWorld().get(4, 1)));
        assertFalse(DefaultRule.defaultIsNeighbor(getMockWorker(1, 1), game.getWorld().get(4, 4)));
    }

    @Test
    public void defaultLevelDifferenceTest() {
        Space lowSpace = game.getWorld().get(1, 1);
        Space highSpace = game.getWorld().get(1, 2);
        Function<Space, Worker> getWorker = space ->
                getMockWorker(space.getPosition().getX(), space.getPosition().getY());

        //same level
        assertTrue(DefaultRule.defaultLevelDifference(getMockWorker(lowSpace), highSpace));

        highSpace = highSpace.addLevel(); // level 1
        // move up 1 level
        assertTrue(DefaultRule.defaultLevelDifference(getMockWorker(lowSpace), highSpace));
        // move down 1 level
        assertTrue(DefaultRule.defaultLevelDifference(getMockWorker(highSpace), lowSpace));

        highSpace = highSpace.addLevel(); // level 2
        // move up 2 levels
        assertFalse(DefaultRule.defaultLevelDifference(getMockWorker(lowSpace), highSpace));
        // move down 2 levels
        assertTrue(DefaultRule.defaultLevelDifference(getMockWorker(highSpace), lowSpace));

        highSpace = highSpace.addLevel(); // level 3
        // move up 3 levels
        assertFalse(DefaultRule.defaultLevelDifference(getMockWorker(lowSpace), highSpace));
        // move down 3 levels
        assertTrue(DefaultRule.defaultLevelDifference(getMockWorker(highSpace), lowSpace));
    }

    @Test
    public void defaultIsFreeFromWorker() {
        Space freeSpace = game.getWorld().get(1, 1);
        Space occupiedSpace = game.getWorld().get(1, 2);
        assertTrue(DefaultRule.defaultIsFreeFromWorker(getMockWorker(freeSpace), occupiedSpace));
        occupiedSpace = occupiedSpace.setWorker(player1.getAllWorkers().get(0).getIdentity());
        assertFalse(DefaultRule.defaultIsFreeFromWorker(getMockWorker(freeSpace), occupiedSpace));
    }

    private Worker getMockWorker(int x, int y) {
        return getMockWorker(this.game.getWorld().get(x, y));
    }

    private Worker getMockWorker(Space space) {
        Worker worker = new MockWorker(this.game.getWorld(), this.game.getRules());
        worker.setStartPosition(space);
        return worker;
    }
}

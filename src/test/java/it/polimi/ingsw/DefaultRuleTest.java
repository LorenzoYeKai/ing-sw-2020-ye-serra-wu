package it.polimi.ingsw;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.rules.DefaultRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultRuleTest {

    Game game;
    Player player1;

    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
    }

    @Test
    void defaultIsNeighborTest(){
        assertTrue(DefaultRule.defaultIsNeighbor(game.getWorld().getSpaces(1, 1), game.getWorld().getSpaces(1, 2)));
        assertFalse(DefaultRule.defaultIsNeighbor(game.getWorld().getSpaces(1, 1), game.getWorld().getSpaces(1, 4)));
        assertFalse(DefaultRule.defaultIsNeighbor(game.getWorld().getSpaces(1, 1), game.getWorld().getSpaces(4, 1)));
        assertFalse(DefaultRule.defaultIsNeighbor(game.getWorld().getSpaces(1, 1), game.getWorld().getSpaces(4, 4)));
    }

    @Test
    void defaultLevelDifferenceTest(){
        Space lowSpace = game.getWorld().getSpaces(1, 1);
        Space highSpace = game.getWorld().getSpaces(1, 2);
        assertTrue(DefaultRule.defaultLevelDifference(lowSpace, highSpace));//same level
        highSpace.addLevel();//level 1
        assertTrue(DefaultRule.defaultLevelDifference(lowSpace, highSpace));//move Up 1 level
        assertTrue(DefaultRule.defaultLevelDifference(highSpace, lowSpace));//move Down 1 level
        highSpace.addLevel();//level 2
        assertFalse(DefaultRule.defaultLevelDifference(lowSpace, highSpace));//move Up 2 levels
        assertTrue(DefaultRule.defaultLevelDifference(highSpace, lowSpace));//move Down 2 levels
        highSpace.addLevel();//level 3
        assertFalse(DefaultRule.defaultLevelDifference(lowSpace, highSpace));//move Up 3 levels
        assertTrue(DefaultRule.defaultLevelDifference(highSpace, lowSpace));//move Down 3 levels
    }

    @Test
    void defaultIsFreeFromWorker(){
        Space freeSpace = game.getWorld().getSpaces(1, 1);
        Space occupiedSpace = game.getWorld().getSpaces(1, 2);
        assertTrue(DefaultRule.defaultIsFreeFromWorker(freeSpace, occupiedSpace));
        occupiedSpace.setWorker(player1.getAllWorkers().get(0));
        assertFalse(DefaultRule.defaultIsFreeFromWorker(freeSpace, occupiedSpace));
    }
}

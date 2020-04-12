package it.polimi.ingsw;

import it.polimi.ingsw.models.game.SpaceData;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.rules.DefaultRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultRuleTest {

    World world;

    @BeforeEach
    void init(){
        world = new World(new Notifiable<SpaceData>() {
            @Override
            public void notify(SpaceData value) {
                System.out.println("Ciao");
            }
        });
    }

    @Test
    void defaultIsNeighborTest(){
        assertTrue(DefaultRule.defaultIsNeighbor(world.getSpaces(1, 1), world.getSpaces(1, 2)));
    }

    @Test
    void defaultLevelDifferenceTest(){
        assertTrue(DefaultRule.defaultLevelDifference(world.getSpaces(1, 1), world.getSpaces(1, 2)));
    }

    @Test
    void defaultIsOccupiedByWorker(){
        assertTrue(DefaultRule.defaultIsOccupied(world.getSpaces(1, 1), world.getSpaces(1, 2)));
    }
}

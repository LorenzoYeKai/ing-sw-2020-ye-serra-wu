package it.polimi.ingsw;

import it.polimi.ingsw.models.game.SpaceData;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.rules.ActualRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActualRuleTest {

    World world;

    @BeforeEach
    void init(){
        world = new World(new Notifiable<SpaceData>() {
            @Override
            public void notify(SpaceData value) {
                System.out.println("Notified!!");
            }
        });
    }

    @Test
    @DisplayName("Testing with no god power")
    void defaultCanMoveThereTest(){
        world.getSpaces(1, 0).addLevel();//1, 0 level 2
        world.getSpaces(1, 0).addLevel();
        world.getSpaces(2, 1).addLevel();//2, 1 level 1
        world.getSpaces(0, 1).setDome();//occupied by dome
        ActualRule rules = new ActualRule(world);
        assertTrue(rules.canMoveThere(world.getSpaces(1, 1), world.getSpaces(2, 1)));
        assertTrue(rules.canMoveThere(world.getSpaces(1, 1), world.getSpaces(1, 2)));
        assertFalse(rules.canMoveThere(world.getSpaces(1, 1), world.getSpaces(1, 4)));
        assertFalse(rules.canMoveThere(world.getSpaces(1, 1), world.getSpaces(1, 0)));
        assertFalse(rules.canMoveThere(world.getSpaces(1, 1), world.getSpaces(0, 1)));
    }



}

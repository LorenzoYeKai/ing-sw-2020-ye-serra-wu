package it.polimi.ingsw;

import it.polimi.ingsw.models.game.SpaceData;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.rules.ActualRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActualRuleTest {


    @Test
    void canMoveThereTest(){
        World world = new World(new Notifiable<SpaceData>() {
            @Override
            public void notify(SpaceData value) {
                System.out.println("Ciao");
            }
        });
        ActualRule rules = new ActualRule(world);
        assertTrue(rules.canMoveThere(world.getSpaces(1, 1), world.getSpaces(1, 2)));
    }

}

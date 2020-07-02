package it.polimi.ingsw.tests;

import it.polimi.ingsw.Notifiable;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceTest {

    @Test
    public void isNeighborTest(){
        World world = new World(new Notifiable<Space>() {
            @Override
            public void notify(Space value) {
                System.out.println("Ciao");
            }
        });
        assertTrue(world.get(1, 1).getPosition().isNeighbor(world.get(1, 2).getPosition()));
    }

    @Test
    public void levelDifferenceTest(){
        World world = new World(new Notifiable<Space>() {
            @Override
            public void notify(Space value) {
                System.out.println("Ciao");
            }
        });
        assertEquals(0, world.get(1, 1).levelDifference(world.get(1, 2)));
    }

}

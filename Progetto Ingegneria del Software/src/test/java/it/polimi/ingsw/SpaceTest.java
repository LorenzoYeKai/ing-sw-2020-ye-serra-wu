package it.polimi.ingsw;

import it.polimi.ingsw.models.game.SpaceData;
import it.polimi.ingsw.models.game.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceTest {

    @Test
    void isNeighborTest(){
        World world = new World(new Notifiable<SpaceData>() {
            @Override
            public void notify(SpaceData value) {
                System.out.println("Ciao");
            }
        });
        assertTrue(world.getSpaces(1, 1).isNeighbor(world.getSpaces(1, 2)));
    }

    @Test
    void levelDifferenceTest(){
        World world = new World(new Notifiable<SpaceData>() {
            @Override
            public void notify(SpaceData value) {
                System.out.println("Ciao");
            }
        });
        assertEquals(0, world.getSpaces(1, 1).levelDifference(world.getSpaces(1, 2)));
    }

}

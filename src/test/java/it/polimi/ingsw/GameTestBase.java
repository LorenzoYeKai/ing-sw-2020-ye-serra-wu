package it.polimi.ingsw;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class GameTestBase {
    protected static void printWorld(World world) {
        System.out.println("┼───┼───┼───┼───┼───┼");
        for(int y = 0; y < World.SIZE; ++y) {
            System.out.print("|");
            for(int x = 0; x < World.SIZE; ++x) {
                Space space = world.get(x, y);
                if(space.getLevel() > 0) {
                    System.out.print(space.getLevel());
                }
                else {
                    System.out.print(" ");
                }

                if(space.isOccupiedByDome()) {
                    System.out.print("^ ");
                }
                else if(space.isOccupiedByWorker()) {
                    System.out.print(space.getWorkerData().getPlayer().charAt(0));
                    System.out.print(space.getWorkerData().getIndex());
                }
                else {
                    System.out.print("  ");
                }

                System.out.print("|");
            }
            System.out.println("\n┼───┼───┼───┼───┼───┼");
        }
    }

    protected final void asserting(List<Space> expected1, List<Space> expected2,
                                   List<Space> actual1, List<Space> actual2) {
        TreeSet<String> expectedSet1 = expected1.stream()
                .map(space -> space.getPosition().toString())
                .collect(Collectors.toCollection(TreeSet::new));
        TreeSet<String> expectedSet2 = expected2.stream()
                .map(space -> space.getPosition().toString())
                .collect(Collectors.toCollection(TreeSet::new));
        TreeSet<String> actualSet1 = actual1.stream()
                .map(space -> space.getPosition().toString())
                .collect(Collectors.toCollection(TreeSet::new));
        TreeSet<String> actualSet2 = actual2.stream()
                .map(space -> space.getPosition().toString())
                .collect(Collectors.toCollection(TreeSet::new));

        assertEquals(expectedSet1, actualSet1);
        assertEquals(expectedSet2, actualSet2);
    }
}

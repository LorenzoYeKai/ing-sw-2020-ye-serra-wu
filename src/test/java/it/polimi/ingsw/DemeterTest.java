package it.polimi.ingsw;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.rules.GodPower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DemeterTest {
    Game game;
    Player player1;

    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
        spaceSetup();
        Space firstWorkerPosition = game.getWorld().getSpaces(1, 1);
        Space secondWorkerPosition = game.getWorld().getSpaces(2, 2);
        player1.getAllWorkers().get(0).setStartPosition(firstWorkerPosition);
        player1.getAllWorkers().get(1).setStartPosition(secondWorkerPosition);
    }

    @Test
    @DisplayName("buildableSpaces without god powers")
    void computeBuildableSpacesTest(){
        ArrayList<Space> expected1 = manualAvailableSpaces1();
        ArrayList<Space> expected2 = manualAvailableSpaces2();
        ArrayList<Space> actual1 = player1.getAllWorkers().get(0).computeBuildableSpaces();
        ArrayList<Space> actual2 = player1.getAllWorkers().get(1).computeBuildableSpaces();
        printing(expected1, expected2, actual1, actual2);
        asserting(expected1, expected2, actual1, actual2);
    }

    @Test
    @DisplayName("Second build with Demeter")
    void demeterPowerTest(){
        game.savePreviousWorld();
        player1.getAllWorkers().get(0).buildBlock(game.getWorld().getSpaces(1, 0));

        System.out.println("Worker 1 (has built) expected [1][0]: ");
        System.out.println("x: [" + player1.getAllWorkers().get(0).previousBuild().getX() + "] y: [" + player1.getAllWorkers().get(0).previousBuild().getY() + "] ");

        assertEquals(1, game.getWorld().getSpaces(1, 0).getLevel()); //Worker 1 has built correctly so the level of [1][0] is now 1
        game.getRules().addBuildRules("demeterPower", GodPower::demeterPower);

        ArrayList<Space> actualSpaces = player1.getAllWorkers().get(0).computeBuildableSpaces();
        ArrayList<Space> expectedSpaces = demeterAvailableSpaces1();

        System.out.println("Worker1:");
        actualSpaces.forEach(space -> System.out.println("x: [" + space.getX() + "] y: [" + space.getY() + "] "));
        //printing Worker 1 expected available spaces
        System.out.println("Worker1 expected:");
        expectedSpaces.forEach(space -> System.out.println("x: [" + space.getX() + "] y: [" + space.getY() + "] "));

        expectedSpaces.forEach(space -> assertTrue(actualSpaces.contains(space)));
        actualSpaces.forEach((space -> assertTrue(expectedSpaces.contains(space))));
    }

    @Test
    @DisplayName("BuildableSpaces with Demeter")
    void buildableSpacesDemeterTest(){
        game.getRules().addBuildRules("demeterPower", GodPower::demeterPower);
        game.savePreviousWorld();
        player1.getAllWorkers().get(0).move(game.getWorld().getSpaces(1, 0));

        ArrayList<Space> expected1 = manualAvailableSpaces3();
        ArrayList<Space> actual1 = player1.getAllWorkers().get(0).computeBuildableSpaces();

        System.out.println("Worker1:");
        actual1.forEach(space -> System.out.println("x: [" + space.getX() + "] y: [" + space.getY() + "] "));
        System.out.println("Worker1 expected:");
        expected1.forEach(space -> System.out.println("x: [" + space.getX() + "] y: [" + space.getY() + "] "));

        expected1.forEach(space -> assertTrue(actual1.contains(space)));
        actual1.forEach((space -> assertTrue(expected1.contains(space))));

        game.savePreviousWorld();
        player1.getAllWorkers().get(0).buildBlock(game.getWorld().getSpaces(2, 0));

        ArrayList<Space> expected2 = manualAvailableSpaces4();
        ArrayList<Space> actual2 = player1.getAllWorkers().get(0).computeBuildableSpaces();

        System.out.println("Worker1 second build:");
        actual2.forEach(space -> System.out.println("x: [" + space.getX() + "] y: [" + space.getY() + "] "));
        System.out.println("Worker1 second build expected:");
        expected2.forEach(space -> System.out.println("x: [" + space.getX() + "] y: [" + space.getY() + "] "));

        expected2.forEach(space -> assertTrue(actual2.contains(space)));
        actual2.forEach((space -> assertTrue(expected2.contains(space))));
    }

    void printing(ArrayList<Space> expected1, ArrayList<Space> expected2, ArrayList<Space> actual1, ArrayList<Space> actual2){
        System.out.println("Worker1:");
        actual1
                .forEach(space -> System.out.println("x: [" + space.getX() + "] y: [" + space.getY() + "] "));
        System.out.println("Worker1 expected:");
        expected1
                .forEach(space -> System.out.println("x: [" + space.getX() + "] y: [" + space.getY() + "] "));
        System.out.println("\nWorker2:");
        actual2
                .forEach(space -> System.out.println("x: [" + space.getX() + "] y: [" + space.getY() + "] "));
        System.out.println("Worker2 expected");
        expected2
                .forEach(space -> System.out.println("x: [" + space.getX() + "] y: [" + space.getY() + "] "));
    }

    void asserting(ArrayList<Space> expected1, ArrayList<Space> expected2, ArrayList<Space> actual1, ArrayList<Space> actual2){
        expected1.forEach(space -> assertTrue(actual1.contains(space)));
        actual1.forEach((space -> assertTrue(expected1.contains(space))));
        expected2.forEach(space -> assertTrue(actual2.contains(space)));
        actual2.forEach((space -> assertTrue(expected2.contains(space))));
    }

    void spaceSetup(){
        World world = game.getWorld();
        world.getSpaces(1, 1).addLevel();//[1][1] level 1
        for(int i = 0; i < 3; i++) world.getSpaces(2, 1).addLevel(); //[2][1] level 3
        for(int i = 0; i < 2; i++) world.getSpaces(2, 2).addLevel(); //[2][2] level 2
        for(int i = 0; i < 3; i++) world.getSpaces(1, 2).addLevel(); //[1][2] level 3 with dome
        for(int i = 0; i < 3; i++) world.getSpaces(4, 0).addLevel();
        world.getSpaces(1, 2).setDome();
    }

    ArrayList<Space> manualAvailableSpaces1(){
        World world = game.getWorld();
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.getSpaces(0, 0));
        availableSpaces.add(world.getSpaces(0, 1));
        availableSpaces.add(world.getSpaces(0, 2));
        availableSpaces.add(world.getSpaces(1, 0));
        availableSpaces.add(world.getSpaces(2, 0));
        return availableSpaces;
    }

    ArrayList<Space> manualAvailableSpaces2(){
        World world = game.getWorld();
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.getSpaces(3, 3));
        availableSpaces.add(world.getSpaces(3, 2));
        availableSpaces.add(world.getSpaces(3, 1));
        availableSpaces.add(world.getSpaces(1, 3));
        availableSpaces.add(world.getSpaces(2, 3));
        return availableSpaces;
    }

    ArrayList<Space> demeterAvailableSpaces1(){
        World world = game.getWorld();
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.getSpaces(0, 0));
        availableSpaces.add(world.getSpaces(0, 1));
        availableSpaces.add(world.getSpaces(0, 2));
        availableSpaces.add(world.getSpaces(2, 0));
        return availableSpaces;
    }

    ArrayList<Space> demeterAvailableSpaces2(){
        World world = game.getWorld();
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.getSpaces(3, 3));
        availableSpaces.add(world.getSpaces(3, 2));
        availableSpaces.add(world.getSpaces(3, 1));
        availableSpaces.add(world.getSpaces(1, 3));
        availableSpaces.add(world.getSpaces(2, 3));
        return availableSpaces;
    }

    ArrayList<Space> manualAvailableSpaces3(){
        World world = game.getWorld();
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.getSpaces(0, 0));
        availableSpaces.add(world.getSpaces(1, 1));
        availableSpaces.add(world.getSpaces(0, 1));
        availableSpaces.add(world.getSpaces(2, 0));
        return availableSpaces;
    }

    ArrayList<Space> manualAvailableSpaces4(){
        World world = game.getWorld();
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.getSpaces(0, 0));
        availableSpaces.add(world.getSpaces(1, 1));
        availableSpaces.add(world.getSpaces(0, 1));
        return availableSpaces;
    }


}

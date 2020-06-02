package it.polimi.ingsw;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.rules.DefaultRule;
import it.polimi.ingsw.models.game.rules.GodPower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApolloTest {

    Game game;
    Player player1;
    Player player2;

    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
        player2 = game.findPlayerByName("player 1");
        spaceSetup();
        Space player1FirstWorkerPosition = game.getWorld().getSpaces(1, 1);
        Space player1SecondWorkerPosition = game.getWorld().getSpaces(2, 2);
        player1.getAllWorkers().get(0).setStartPosition(player1FirstWorkerPosition);
        player1.getAllWorkers().get(1).setStartPosition(player1SecondWorkerPosition);
        Space player2FirstWorkerPosition = game.getWorld().getSpaces(2, 0);
        Space player2SecondWorkerPosition = game.getWorld().getSpaces(3, 2);
        player2.getAllWorkers().get(0).setStartPosition(player2FirstWorkerPosition);
        player2.getAllWorkers().get(1).setStartPosition(player2SecondWorkerPosition);
    }

    @Test
    @DisplayName("availableSpaces without god powers")
    void computeAvailableSpacesTest(){
        ArrayList<Space> expected1 = manualAvailableSpaces1();
        ArrayList<Space> expected2 = manualAvailableSpaces2();
        ArrayList<Space> actual1 = player1.getAllWorkers().get(0).computeAvailableSpaces();
        ArrayList<Space> actual2 = player1.getAllWorkers().get(1).computeAvailableSpaces();
        printing(expected1, expected2, actual1, actual2);
        asserting(expected1, expected2, actual1, actual2);
    }

    @Test
    @DisplayName("availableSpaces with Apollo")
    void apolloPowerTest(){
        game.getRules().addMovementRules("apolloPower", GodPower::apolloPower);
        game.getRules().getMovementRules().remove("defaultIsFreeFromWorker");
        ArrayList<Space> expected1 = manualApolloAvailableSpaces1();
        ArrayList<Space> expected2 = manualApolloAvailableSpaces2();
        ArrayList<Space> actual1 = player1.getAllWorkers().get(0).computeAvailableSpaces();
        ArrayList<Space> actual2 = player1.getAllWorkers().get(1).computeAvailableSpaces();
        printing(expected1, expected2, actual1, actual2);
        asserting(expected1, expected2, actual1, actual2);
        game.getRules().getMovementRules().remove("apolloPower");
        game.getRules().addMovementRules("defaultIsFreeFromWorker", DefaultRule::defaultIsFreeFromWorker);
    }

    @Test
    @DisplayName("Swap")
    void swapTest(){
        Worker zero = player1.getAllWorkers().get(0);
        Worker opponent = player2.getAllWorkers().get(0);
        game.savePreviousWorld();
        zero.swap(opponent, game.getWorld().getSpaces(2, 0));
        assertEquals(zero.getCurrentSpace(), opponent.previousSpace());
        assertEquals(opponent.getCurrentSpace(), zero.previousSpace());
        System.out.println("Worker 0, player 1 previous space:");
        System.out.println("[" + zero.previousSpace().getX() + "] [" + zero.previousSpace().getY() + "]\n");
        System.out.println("Worker 0, player 1 current space:");
        System.out.println("[" + zero.getX() + "] [" + zero.getY() + "]\n");
        System.out.println("Worker 0, player 2 previous space:");
        System.out.println("[" + opponent.previousSpace().getX() + "] [" + opponent.previousSpace().getY() + "]\n");
        System.out.println("Worker 0, player 2 current space:");
        System.out.println("[" + opponent.getX() + "] [" + opponent.getY() + "]");
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
        world.getSpaces(1, 2).setDome();
    }

    ArrayList<Space> manualAvailableSpaces1(){
        World world = game.getWorld();
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.getSpaces(0, 0));
        availableSpaces.add(world.getSpaces(0, 1));
        availableSpaces.add(world.getSpaces(0, 2));
        availableSpaces.add(world.getSpaces(1, 0));

        return availableSpaces;
    }

    ArrayList<Space> manualAvailableSpaces2(){
        World world = game.getWorld();
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.getSpaces(3, 3));

        availableSpaces.add(world.getSpaces(3, 1));
        availableSpaces.add(world.getSpaces(1, 3));
        availableSpaces.add(world.getSpaces(2, 3));
        availableSpaces.add(world.getSpaces(2, 1));
        return availableSpaces;
    }

    ArrayList<Space> manualApolloAvailableSpaces1(){
        World world = game.getWorld();
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.getSpaces(0, 0));
        availableSpaces.add(world.getSpaces(0, 1));
        availableSpaces.add(world.getSpaces(0, 2));
        availableSpaces.add(world.getSpaces(1, 0));
        availableSpaces.add(world.getSpaces(2, 0));
        return availableSpaces;
    }

    ArrayList<Space> manualApolloAvailableSpaces2(){
        World world = game.getWorld();
        ArrayList<Space> availableSpaces = new ArrayList<Space>();
        availableSpaces.add(world.getSpaces(3, 3));
        availableSpaces.add(world.getSpaces(3, 2));
        availableSpaces.add(world.getSpaces(3, 1));
        availableSpaces.add(world.getSpaces(1, 3));
        availableSpaces.add(world.getSpaces(2, 3));
        availableSpaces.add(world.getSpaces(2, 1));
        return availableSpaces;
    }
}
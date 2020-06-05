package it.polimi.ingsw;

import it.polimi.ingsw.models.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SavePreviousWorldTest {

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
    @DisplayName("save previous World after move")
    void savePreviousWorldMoveTest(){
        game.savePreviousWorld();
        System.out.println("Phase: " + game.getTurnPhase());
        //assertEquals(game.getPreviousWorld(), game.getPreviousWorldA());
        player1.getAllWorkers().get(0).move(game.getWorld().getSpaces(1, 0));

        System.out.println("World:");
        printIsOccupiedByWorker(game.getWorld());
        System.out.println(("Previous World:"));
        printIsOccupiedByWorker(game.getPreviousWorld());

        assertTrue(player1.getAllWorkers().get(0).hasMoved());
        if(player1.getAllWorkers().get(0).hasMoved()){
            System.out.println("Worker 0 has moved!");
        }

        assertFalse(player1.getAllWorkers().get(1).hasMoved());
        if(!player1.getAllWorkers().get(1).hasMoved()){
            System.out.println("Worker 1 NOT has moved!");
        }
        //Worker 1 moves for the second time
        game.savePreviousWorld();

        player1.getAllWorkers().get(0).move(game.getWorld().getSpaces(0, 0));
        System.out.println("Phase: " + game.getTurnPhase());
        //assertEquals(game.getPreviousWorld(), game.getPreviousWorldA());
        System.out.println("World second movement:");
        printIsOccupiedByWorker(game.getWorld());
        System.out.println(("Previous World second movement:"));
        printIsOccupiedByWorker(game.getPreviousWorld());

        assertTrue(player1.getAllWorkers().get(0).hasMoved());
        if(player1.getAllWorkers().get(0).hasMoved()){
            System.out.println("Worker 0 has moved the second time!");
        }

        assertFalse(player1.getAllWorkers().get(1).hasMoved());
        if(!player1.getAllWorkers().get(1).hasMoved()){
            System.out.println("Worker 1 NOT has moved!");
        }

        game.savePreviousWorld();
        player1.getAllWorkers().get(0).buildBlock(game.getWorld().getSpaces(0, 1));
        System.out.println("World:");
        printIsOccupiedByWorker(game.getWorld());
        System.out.println(("Previous World:"));
        printIsOccupiedByWorker(game.getPreviousWorld());
        if(!player1.getAllWorkers().get(0).hasMoved()){
            System.out.println("Worker 0 has NOT moved, instead it built");
        }
    }

    @Test
    @DisplayName("save previous World after Build")
    void savePreviousWorldBuildBlockTest(){
        game.savePreviousWorld();
        player1.getAllWorkers().get(0).buildBlock(game.getWorld().getSpaces(1, 0));
        System.out.println("World space levels:");
        printSpaceLevels(game.getWorld());
        System.out.println(("Previous World space levels:"));
        printSpaceLevels(game.getPreviousWorld());
        assertTrue(player1.getAllWorkers().get(0).hasBuiltBlock());
    }

    void spaceSetup(){
        World world = game.getWorld();
        world.getSpaces(1, 1).addLevel();//[1][1] level 1
        for(int i = 0; i < 3; i++) world.getSpaces(2, 1).addLevel(); //[2][1] level 3
        for(int i = 0; i < 2; i++) world.getSpaces(2, 2).addLevel(); //[2][2] level 2
        for(int i = 0; i < 3; i++) world.getSpaces(1, 2).addLevel(); //[1][2] level 3 with dome
        world.getSpaces(1, 2).setDome();
    }

    void printIsOccupiedByWorker(World world){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(world.getSpaces(i, j).isOccupiedByWorker()){
                    System.out.println("[" + i + "] [" + j + "] is occupied by a worker!");
                }
            }
        }
    }

    void printSpaceLevels(World world){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(world.getSpaces(i, j).getLevel() > 0){
                    System.out.println("[" + i + "] [" + j + "] level: " + world.getSpaces(i, j).getLevel());
                }
            }
        }
    }
}

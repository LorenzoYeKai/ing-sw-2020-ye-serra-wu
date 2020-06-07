package it.polimi.ingsw;

import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UndoTest {

    Game game;
    GameController controller;
    Player player1;
    Player player2;

    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2");
        controller = new GameController(names);
        game = controller.getGame();
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
        player2 = game.findPlayerByName("player 1");
        spaceSetup();
        Space firstWorkerPosition = game.getWorld().getSpaces(1, 1);
        Space secondWorkerPosition = game.getWorld().getSpaces(2, 2);
        player1.getAllWorkers().get(0).setStartPosition(firstWorkerPosition);
        player1.getAllWorkers().get(1).setStartPosition(secondWorkerPosition);
        Space player2FirstWorkerPosition = game.getWorld().getSpaces(2, 0);
        Space player2SecondWorkerPosition = game.getWorld().getSpaces(3, 2);
        player2.getAllWorkers().get(0).setStartPosition(player2FirstWorkerPosition);
        player2.getAllWorkers().get(1).setStartPosition(player2SecondWorkerPosition);
    }

    @Test
    @DisplayName("Undo with a move:")
    void undoMoveTest(){

        //Worker moves

        game.savePreviousWorld();
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

        //UNDO after 1 move, the previousWorld list should be empty after undo

        controller.undo();

        System.out.println("World:");
        printIsOccupiedByWorker(game.getWorld());
        assertThrows(UnsupportedOperationException.class, () -> game.getPreviousWorld());

        for(Player p : game.getListOfPlayers()){
            p.getAllWorkers().forEach(w -> assertTrue(game.getWorld().getWorkersInWorld().contains(w)));
        }
    }

    @Test
    @DisplayName("Undo with a move and a build:")
    void undoMoveAndBuildTest(){

        //Worker moves

        System.out.println("Worker 0 moves to [1][0]");
        game.savePreviousWorld();
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

        //Worker builds

        System.out.println("Worker 0 builds in [1][1]");
        game.savePreviousWorld();
        player1.getAllWorkers().get(0).buildBlock(game.getWorld().getSpaces(1, 1));
        System.out.println("World space levels:");
        printSpaceLevels(game.getWorld());
        System.out.println(("Previous World space levels:"));
        printSpaceLevels(game.getPreviousWorld());
        assertTrue(player1.getAllWorkers().get(0).hasBuiltBlock());

        //UNDO after 1 move and 1 build

        System.out.println("UNDO after build!");
        controller.undo();

        System.out.println("World:");
        printIsOccupiedByWorker(game.getWorld());
        System.out.println(("Previous World:"));
        printIsOccupiedByWorker(game.getPreviousWorld());
        System.out.println("World space levels:");
        printSpaceLevels(game.getWorld());
        System.out.println(("Previous World space levels:"));
        printSpaceLevels(game.getPreviousWorld());
        assertFalse(player1.getAllWorkers().get(0).hasBuiltBlock());

        for(Player p : game.getListOfPlayers()){
            p.getAllWorkers().forEach(w -> assertTrue(game.getWorld().getWorkersInWorld().contains(w)));
        }
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

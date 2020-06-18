package it.polimi.ingsw.ControllerTest;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    GameController controller;

    @BeforeEach
    void init() {
        List<String> nickname = List.of("player1", "player2", "player3");
        controller = new GameController(nickname);
        controller.getGame().setCurrentPlayer(0);
    }

    @Test
    @DisplayName("Aggiuntapoteri alla partita")
    void addAvailableGodsTest() {
        controller.addAvailableGods(GodType.APOLLO);
        controller.addAvailableGods(GodType.ARTEMIS);
        controller.addAvailableGods(GodType.ATHENA);
        assertTrue(controller.getGame().getAvailableGods().contains(GodType.APOLLO));
        assertTrue(controller.getGame().getAvailableGods().contains(GodType.ARTEMIS));
        assertTrue(controller.getGame().getAvailableGods().contains(GodType.ATHENA));
        assertFalse(controller.getGame().getAvailableGods().contains(GodType.ATLAS));
        assertFalse(controller.getGame().getAvailableGods().contains(GodType.DEMETER));
        assertFalse(controller.getGame().getAvailableGods().contains(GodType.HEPHAESTUS));
        assertFalse(controller.getGame().getAvailableGods().contains(GodType.MINOTAUR));
        assertFalse(controller.getGame().getAvailableGods().contains(GodType.PAN));
        assertFalse(controller.getGame().getAvailableGods().contains(GodType.PROMETHEUS));
    }

    @Test
    @DisplayName("Aggiuntapoteri alla partita")
    void removeAvailableGodsTest() {
        assertFalse(controller.getGame().getAvailableGods().contains(GodType.APOLLO));
        controller.addAvailableGods(GodType.APOLLO);
        assertTrue(controller.getGame().getAvailableGods().contains(GodType.APOLLO));
        controller.removeAvailableGod(GodType.APOLLO);
        assertFalse(controller.getGame().getAvailableGods().contains(GodType.APOLLO));
    }

    @Test
    @DisplayName("NextTurn")
    void nextTurnTest() {
        controller.setCurrentPlayer(0);
        controller.nextTurn();
        assertNotEquals(controller.getGame().getCurrentPlayer(), controller.getGame().getListOfPlayers().get(0));
        assertEquals(controller.getGame().getCurrentPlayer(), controller.getGame().getListOfPlayers().get(1));

        controller.setCurrentPlayer(1);
        controller.nextTurn();
        assertNotEquals(controller.getGame().getCurrentPlayer(), controller.getGame().getListOfPlayers().get(1));
        assertEquals(controller.getGame().getCurrentPlayer(), controller.getGame().getListOfPlayers().get(2));

        controller.setCurrentPlayer(2);
        controller.nextTurn();
        assertNotEquals(controller.getGame().getCurrentPlayer(), controller.getGame().getListOfPlayers().get(2));
        assertEquals(controller.getGame().getCurrentPlayer(), controller.getGame().getListOfPlayers().get(0));

    }

    @Test
    @DisplayName("Place test")
    void placeTest() throws NotExecutedException {
        controller.getGame().setCurrentPlayer(0);
        assertFalse(controller.getGame().getWorld().getSpaces(0, 0).isOccupiedByWorker());
        controller.place(controller.getGame().getCurrentPlayer().getAllWorkers().get(0), controller.getGame().getWorld().getSpaces(0, 0));

    }

    @Test
    @DisplayName("Place test")
    void moveTest() throws NotExecutedException {
        spaceSetup();
        Worker worker = controller.getGame().getCurrentPlayer().getAllWorkers().get(0);
        assertFalse(controller.getGame().getWorld().getSpaces(2,1).isOccupiedByWorker());
        controller.move( worker ,controller.getGame().getWorld().getSpaces(2,1));
        assertTrue(controller.getGame().getWorld().getSpaces(2,1).isOccupiedByWorker());
        controller.move( worker ,controller.getGame().getWorld().getSpaces(1,1));
        assertTrue(controller.getGame().getWorld().getSpaces(1,1).isOccupiedByWorker());
    }
    @Test
    @DisplayName("Build test")
    void buildTest() throws NotExecutedException {
        spaceSetup();
        Worker worker = controller.getGame().getCurrentPlayer().getAllWorkers().get(0);
        controller.build(worker ,controller.getGame().getWorld().getSpaces(1,1));
        controller.buildDome(worker ,controller.getGame().getWorld().getSpaces(2,1));
    }
    @Test
    @DisplayName("fasi test")
    void phaseTest(){
        controller.setupGame();
        assertEquals(controller.getGame().getStatus(), GameStatus.SETUP);
        controller.chooseGods();
        assertEquals(controller.getGame().getStatus(), GameStatus.CHOOSING_GODS);
        controller.placeWorkers();
        assertEquals(controller.getGame().getStatus(), GameStatus.PLACING);
        controller.playGame();
        assertEquals(controller.getGame().getStatus(), GameStatus.PLAYING);

    }

    @Test
    @DisplayName("fasi test")
    void setPlayerGodTest(){
        controller.setPlayerGod(controller.getGame().getListOfPlayers().get(0),GodType.APOLLO);
    }

    @Test
    @DisplayName("selectWorkerTest")
    void selectWorkerTest(){
        controller.selectWorker(0);
        assertEquals(0, controller.getGame().getCurrentPlayer().getIndexSelectedWorker());
    }

    @Test
    @DisplayName("resetTurnTest")
    void resetTurnTest() throws NotExecutedException {
        spaceSetup();
        controller.getGame().savePreviousWorld();
        assertNotNull(controller.getGame().getPreviousWorld());
        controller.resetTurn();
        assertThrows(UnsupportedOperationException.class, () -> controller.getGame().getPreviousWorld());
        assertEquals(controller.getGame().getCurrentPlayer().getIndexSelectedWorker(), -1);

    }





    void spaceSetup() throws NotExecutedException {
        World world = controller.getGame().getWorld();
        world.getSpaces(1, 1).addLevel();//[1][1] level 1
        for(int i = 0; i < 3; i++) world.getSpaces(2, 1).addLevel(); //[2][1] level 3
        for(int i = 0; i < 2; i++) world.getSpaces(2, 2).addLevel(); //[2][2] level 2
        for(int i = 0; i < 3; i++) world.getSpaces(1, 2).addLevel(); //[1][2] level 3 with dome
        world.getSpaces(1, 2).setDome();
        world.getSpaces(0,0).addLevel();
        controller.place(controller.getGame().getCurrentPlayer().getAllWorkers().get(0),controller.getGame().getWorld().getSpaces(2,2));
    }


}

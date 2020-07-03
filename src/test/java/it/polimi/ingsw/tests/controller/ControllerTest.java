package it.polimi.ingsw.tests.controller;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.tests.TestGameController;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    TestGameController controller;

    @BeforeEach
    public void init() {
        List<String> nickname = List.of("player1", "player2", "player3");
        controller = new TestGameController(nickname);
        controller.getGame().setCurrentPlayer(0);
    }

    @Test
    @DisplayName("Aggiuntapoteri alla partita")
    public void addAvailableGodsTest() {
        controller.getGame().setStatus(GameStatus.SETUP);
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
    @DisplayName("Add gods to the match")
    public void removeAvailableGodsTest() {
        controller.getGame().setStatus(GameStatus.SETUP);
        assertFalse(controller.getGame().getAvailableGods().contains(GodType.APOLLO));
        controller.addAvailableGods(GodType.APOLLO);
        assertTrue(controller.getGame().getAvailableGods().contains(GodType.APOLLO));
        controller.removeAvailableGod(GodType.APOLLO);
        assertFalse(controller.getGame().getAvailableGods().contains(GodType.APOLLO));
    }

    @Test
    @DisplayName("NextTurn")
    public void nextTurnTest() throws NotExecutedException {
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
    public void placeTest() throws NotExecutedException {
        controller.getGame().setCurrentPlayer(0);
        assertFalse(controller.getGame().getWorld().get(0, 0).isOccupiedByWorker());
        controller.place(controller.getGame().getCurrentPlayer().getAllWorkers().get(0), controller.getGame().getWorld().get(0, 0));

    }

    @Test
    @DisplayName("Place test")
    public void moveTest() throws NotExecutedException {
        spaceSetup();
        Worker worker = controller.getGame().getCurrentPlayer().getAllWorkers().get(0);
        assertFalse(controller.getGame().getWorld().get(2, 1).isOccupiedByWorker());
        controller.move(worker, controller.getGame().getWorld().get(2, 1));
        assertTrue(controller.getGame().getWorld().get(2, 1).isOccupiedByWorker());
        controller.move(worker, controller.getGame().getWorld().get(1, 1));
        assertTrue(controller.getGame().getWorld().get(1, 1).isOccupiedByWorker());
    }

    @Test
    @DisplayName("Build test")
    public void buildTest() throws NotExecutedException {
        spaceSetup();
        Worker worker = controller.getGame().getCurrentPlayer().getAllWorkers().get(0);
        controller.build(worker, controller.getGame().getWorld().get(1, 1));
        controller.buildDome(worker, controller.getGame().getWorld().get(2, 1));
    }

    @Test
    @DisplayName("Phase test")
    public void phaseTest() throws NotExecutedException {
        controller.getGame().setStatus(GameStatus.SETUP);
        assertEquals(controller.getGame().getStatus(), GameStatus.SETUP);
        controller.setGameStatus(GameStatus.CHOOSING_GODS);
        assertEquals(controller.getGame().getStatus(), GameStatus.CHOOSING_GODS);
        controller.setGameStatus(GameStatus.PLACING);
        assertEquals(controller.getGame().getStatus(), GameStatus.PLACING);
        controller.setGameStatus(GameStatus.PLAYING);
        assertEquals(controller.getGame().getStatus(), GameStatus.PLAYING);

    }

    @Test
    @DisplayName("Set Player God test")
    public void setPlayerGodTest() throws NotExecutedException {
        controller.getGame().setStatus(GameStatus.SETUP);
        controller.getGame().addAvailableGods(GodType.APOLLO);
        controller.getGame().setStatus(GameStatus.CHOOSING_GODS);
        controller.setPlayerGod(controller.getGame().getListOfPlayers().get(0).getName(), GodType.APOLLO);
    }

    @Test
    @DisplayName("selectWorkerTest")
    public void selectWorkerTest() {
        controller.selectWorker(0);
        assertEquals(0, controller.getGame().getCurrentPlayer().getIndexSelectedWorker());
    }

    @Test
    @DisplayName("resetTurnTest")
    public void resetTurnTest() throws NotExecutedException {
        spaceSetup();
        assertNotNull(controller.getGame().getPreviousWorld());
        controller.resetTurn();
        assertThrows(UnsupportedOperationException.class, () -> controller.getGame().getPreviousWorld());
        assertEquals(controller.getGame().getCurrentPlayer().getIndexSelectedWorker(), -1);
    }

    void spaceSetup() throws NotExecutedException {
        World world = controller.getGame().getWorld();
        world.update(world.get(1, 1).addLevel());//[1][1] level 1
        for (int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); //[2][1] level 3
        for (int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); //[2][2] level 2
        for (int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); //[1][2] level 3 with dome
        world.update(world.get(1, 2).setDome());
        world.update(world.get(0, 0).addLevel());
        controller.place(controller.getGame().getCurrentPlayer().getAllWorkers().get(0),
                world.get(2, 2));
    }


}

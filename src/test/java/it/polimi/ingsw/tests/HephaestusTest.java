package it.polimi.ingsw.tests;

import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodFactory;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HephaestusTest {

    private Game game;
    private Player player1;

    @BeforeEach
    public void init() {
        List<String> names = List.of("player 1", "player 2");
        game = new Game(names);
        game.findPlayerByName("player 2").setGod(new GodFactory().getGod(GodType.HEPHAESTUS));
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentPlayer(1);
        player1 = game.getCurrentPlayer();
        spaceSetup();
        Space firstWorkerPosition = game.getWorld().get(1, 1);
        Space secondWorkerPosition = game.getWorld().get(2, 2);
        player1.getAllWorkers().get(0).setStartPosition(firstWorkerPosition);
        player1.getAllWorkers().get(1).setStartPosition(secondWorkerPosition);
        game.clearPreviousWorlds();
    }

    @Test
    @DisplayName("buildableSpaces without god powers")
    public void computeBuildableSpacesTest() {
        game.getCurrentPlayer().selectWorker(0);
        game.getCurrentPlayer().getAllWorkers().get(0).move(game.getWorld().get(0,1));
        assertTrue(game.getCurrentPlayer().getAllWorkers().get(0).computeBuildableSpaces().contains(game.getWorld().get(0,0)));
        assertFalse(game.getCurrentPlayer().getAllWorkers().get(0).computeBuildableSpaces().contains(game.getWorld().get(2,1)));
    }

    @Test
    @DisplayName("Second build with Hephaestus")
    public void hephaestusPowerTest() {
        game.getCurrentPlayer().selectWorker(0);

        game.getCurrentPlayer().getAllWorkers().get(0).move(game.getWorld().get(0,1));
        game.getCurrentPlayer().getAllWorkers().get(0).buildBlock(game.getWorld().get(0,0));
        assertFalse(game.getCurrentPlayer().getAllWorkers().get(0).computeBuildableSpaces().contains(game.getWorld().get(2,0)));
        assertTrue(game.getRules().canBuildThere(game.getCurrentPlayer().getAllWorkers().get(0),game.getWorld().get(0,0)));




    }
    private void spaceSetup() {
        World world = game.getWorld();
        world.update(world.get(1, 1).addLevel()); //[1][1] level 1
        for (int i = 0; i < 3; i++) world.update(world.get(2, 1).addLevel()); //[2][1] level 3
        for (int i = 0; i < 2; i++) world.update(world.get(2, 2).addLevel()); //[2][2] level 2
        for (int i = 0; i < 3; i++) world.update(world.get(1, 2).addLevel()); //[1][2] level 3 with dome
        for (int i = 0; i < 3; i++) world.update(world.get(4, 0).addLevel());
        world.update(world.get(1, 2).setDome());
    }


}

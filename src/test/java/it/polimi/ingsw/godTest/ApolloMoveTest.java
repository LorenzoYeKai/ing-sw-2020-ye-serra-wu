package it.polimi.ingsw.godTest;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ApolloMoveTest {
        Game game;
        Player player1,player2;
        GameController controller;

        @BeforeEach
        void init(){
            List<String> names = List.of("player 1", "player 2");
            controller = new GameController(names);
            controller.getGame().setCurrentPlayer(0);
            player1 = controller.getGame().getCurrentPlayer();
            player2 = controller.getGame().getListOfPlayers().get(1);
            spaceSetup();
            Space firstWorkerPosition = controller.getGame().getWorld().getSpaces(1, 1);
            Space secondWorkerPosition = controller.getGame().getWorld().getSpaces(2, 4);
            Space thirdWorkerPosition = controller.getGame().getWorld().getSpaces(0, 1);
            Space fourthWorkerPosition = controller.getGame().getWorld().getSpaces(3, 2);
            controller.getGame().getCurrentPlayer().getAllWorkers().get(0).setStartPosition(firstWorkerPosition);
            controller.getGame().getCurrentPlayer().getAllWorkers().get(1).setStartPosition(secondWorkerPosition);
            controller.getGame().getListOfPlayers().get(1).getAllWorkers().get(0).setStartPosition(thirdWorkerPosition);
            controller.getGame().getListOfPlayers().get(1).getAllWorkers().get(1).setStartPosition(fourthWorkerPosition);

        }

        @Test
        @DisplayName("Check Apollo Power" )
        void apolloMoveTest() throws NotExecutedException  { ;
            controller.getGame().getCurrentPlayer().setGod(GodType.APOLLO);
            controller.getGame().getCurrentPlayer().getGod().activateGodPower(controller.getGame().getCurrentPlayer().getAllWorkers().get(0));
            controller.move(controller.getGame().getCurrentPlayer().getAllWorkers().get(0),controller.getGame().getWorld().getSpaces(0,1));
            assertTrue(controller.getGame().getCurrentPlayer().getAllWorkers().get(0).getCurrentSpace().equals(controller.getGame().getWorld().getSpaces(0,1)));
            controller.getGame().getCurrentPlayer().getGod().deactivateGodPower(controller.getGame().getCurrentPlayer().getAllWorkers().get(0));
        }


        void spaceSetup(){
            World world = controller.getGame().getWorld();
            world.getSpaces(1, 1).addLevel();//[1][1] level 1
            for(int i = 0; i < 3; i++) world.getSpaces(2, 1).addLevel(); //[2][1] level 3
            for(int i = 0; i < 2; i++) world.getSpaces(2, 2).addLevel(); //[2][2] level 2
            for(int i = 0; i < 3; i++) world.getSpaces(1, 2).addLevel(); //[1][2] level 3 with dome
            world.getSpaces(1, 2).setDome();
        }

}

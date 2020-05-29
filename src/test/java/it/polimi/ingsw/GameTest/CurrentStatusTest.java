package it.polimi.ingsw.GameTest;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CurrentStatusTest {

        Game game;
        @BeforeEach
        void init(){
            List<String> names = List.of("player 1", "player 2","player 3");
            game = new Game(names);

        }
        @Test
        @DisplayName("Test SU CurrentStatus")
        void getCurrentStatusTest ()
        {
            game.setStatus(GameStatus.PLAYER_JOINING);
            assertTrue(game.getStatus().equals(GameStatus.PLAYER_JOINING));
            assertFalse(game.getStatus().equals(GameStatus.SETUP));
            assertFalse(game.getStatus().equals(GameStatus.CHOOSING_GODS));
            assertFalse(game.getStatus().equals(GameStatus.PLACING));
            assertFalse(game.getStatus().equals(GameStatus.PLAYING));
            assertFalse(game.getStatus().equals(GameStatus.ENDED));

            game.setStatus(GameStatus.SETUP);
            assertTrue(game.getStatus().equals(GameStatus.SETUP));
            assertFalse(game.getStatus().equals(GameStatus.PLAYER_JOINING));
            assertFalse(game.getStatus().equals(GameStatus.CHOOSING_GODS));
            assertFalse(game.getStatus().equals(GameStatus.PLACING));
            assertFalse(game.getStatus().equals(GameStatus.PLAYING));
            assertFalse(game.getStatus().equals(GameStatus.ENDED));


            game.setStatus(GameStatus.CHOOSING_GODS);
            assertTrue(game.getStatus().equals(GameStatus.CHOOSING_GODS));
            assertFalse(game.getStatus().equals(GameStatus.SETUP));
            assertFalse(game.getStatus().equals(GameStatus.PLAYER_JOINING));
            assertFalse(game.getStatus().equals(GameStatus.PLACING));
            assertFalse(game.getStatus().equals(GameStatus.PLAYING));
            assertFalse(game.getStatus().equals(GameStatus.ENDED));

            game.setStatus(GameStatus.PLACING);
            assertTrue(game.getStatus().equals(GameStatus.PLACING));
            assertFalse(game.getStatus().equals(GameStatus.SETUP));
            assertFalse(game.getStatus().equals(GameStatus.CHOOSING_GODS));
            assertFalse(game.getStatus().equals(GameStatus.PLAYER_JOINING));
            assertFalse(game.getStatus().equals(GameStatus.PLAYING));
            assertFalse(game.getStatus().equals(GameStatus.ENDED));

            game.setStatus(GameStatus.PLAYING);
            assertTrue(game.getStatus().equals(GameStatus.PLAYING));
            assertFalse(game.getStatus().equals(GameStatus.SETUP));
            assertFalse(game.getStatus().equals(GameStatus.CHOOSING_GODS));
            assertFalse(game.getStatus().equals(GameStatus.PLACING));
            assertFalse(game.getStatus().equals(GameStatus.PLAYER_JOINING));
            assertFalse(game.getStatus().equals(GameStatus.ENDED));

            game.setStatus(GameStatus.ENDED);
            assertFalse(game.getStatus().equals(GameStatus.SETUP));
            assertFalse(game.getStatus().equals(GameStatus.CHOOSING_GODS));
            assertFalse(game.getStatus().equals(GameStatus.PLACING));
            assertFalse(game.getStatus().equals(GameStatus.PLAYING));
            assertFalse(game.getStatus().equals(GameStatus.PLAYER_JOINING));
            assertTrue(game.getStatus().equals(GameStatus.ENDED));
        }
}

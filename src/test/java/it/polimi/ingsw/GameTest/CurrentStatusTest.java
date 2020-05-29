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
        void CurrentStatusTest ()
        {
            game.setStatus(GameStatus.PLAYER_JOINING);
            assertEquals(game.getStatus(), GameStatus.PLAYER_JOINING);
            assertNotEquals(game.getStatus(), GameStatus.SETUP);
            assertNotEquals(game.getStatus(), GameStatus.CHOOSING_GODS);
            assertNotEquals(game.getStatus(), GameStatus.PLACING);
            assertNotEquals(game.getStatus(), GameStatus.PLAYING);
            assertNotEquals(game.getStatus(), GameStatus.ENDED);

            game.setStatus(GameStatus.SETUP);
            assertNotEquals(game.getStatus(), GameStatus.PLAYER_JOINING);
            assertEquals(game.getStatus(), GameStatus.SETUP);
            assertNotEquals(game.getStatus(), GameStatus.CHOOSING_GODS);
            assertNotEquals(game.getStatus(), GameStatus.PLACING);
            assertNotEquals(game.getStatus(), GameStatus.PLAYING);
            assertNotEquals(game.getStatus(), GameStatus.ENDED);

            game.setStatus(GameStatus.CHOOSING_GODS);
            assertNotEquals(game.getStatus(), GameStatus.PLAYER_JOINING);
            assertNotEquals(game.getStatus(), GameStatus.SETUP);
            assertEquals(game.getStatus(), GameStatus.CHOOSING_GODS);
            assertNotEquals(game.getStatus(), GameStatus.PLACING);
            assertNotEquals(game.getStatus(), GameStatus.PLAYING);
            assertNotEquals(game.getStatus(), GameStatus.ENDED);

            game.setStatus(GameStatus.PLACING);
            assertNotEquals(game.getStatus(), GameStatus.PLAYER_JOINING);
            assertNotEquals(game.getStatus(), GameStatus.SETUP);
            assertNotEquals(game.getStatus(), GameStatus.CHOOSING_GODS);
            assertEquals(game.getStatus(), GameStatus.PLACING);
            assertNotEquals(game.getStatus(), GameStatus.PLAYING);
            assertNotEquals(game.getStatus(), GameStatus.ENDED);

            game.setStatus(GameStatus.PLAYING);
            assertNotEquals(game.getStatus(), GameStatus.PLAYER_JOINING);
            assertNotEquals(game.getStatus(), GameStatus.SETUP);
            assertNotEquals(game.getStatus(), GameStatus.CHOOSING_GODS);
            assertNotEquals(game.getStatus(), GameStatus.PLACING);
            assertEquals(game.getStatus(), GameStatus.PLAYING);
            assertNotEquals(game.getStatus(), GameStatus.ENDED);

            game.setStatus(GameStatus.ENDED);
            assertNotEquals(game.getStatus(), GameStatus.PLAYER_JOINING);
            assertNotEquals(game.getStatus(), GameStatus.SETUP);
            assertNotEquals(game.getStatus(), GameStatus.CHOOSING_GODS);
            assertNotEquals(game.getStatus(), GameStatus.PLACING);
            assertNotEquals(game.getStatus(), GameStatus.PLAYING);
            assertEquals(game.getStatus(), GameStatus.ENDED);

        }
}

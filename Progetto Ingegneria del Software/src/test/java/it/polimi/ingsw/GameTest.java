package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class GameTest {

    Game game;
    GodType[] sample = {GodType.ATHENA, GodType.APOLLO};;

    @BeforeEach
    void twoPlayerGameCreation(){
        String[] names = {"peppino", "giuseppi"};
        game = new Game(2, names);
    }

    @Test
    @DisplayName("Testing setAvailable Gods")
    void availableGods(){
        game.setAvailableGods(sample);
        assertEquals(2, game.getNumberOfAvailableGods(), "setAvailableGods should be as large as numberOfPlayers");
        assertTrue(game.isGodAvailable(GodType.ATHENA));
        assertTrue(game.isGodAvailable(GodType.APOLLO));
    }

    @Nested
    class availableGodsExceptions{
        @Test
        @DisplayName("Testing availableGods already created: ")
        void alreadyCreated(){
            game.setAvailableGods(sample);
            GodType[] sample2 = {GodType.ARTEMIS, GodType.MINOTAUR};
            assertThrows(UnsupportedOperationException.class, () -> game.setAvailableGods(sample2), "Should throw exception");

        }

        @Test
        @DisplayName("Testing numberOfPlayers and numberOfAvailableGods not compatibele")
        void numberNotCompatible(){
            GodType[] sample2 = {GodType.ATHENA, GodType.APOLLO, GodType.ATLAS};
            assertThrows(IllegalArgumentException.class, () -> game.setAvailableGods(sample2), "Should throw exception");
        }
    }


}

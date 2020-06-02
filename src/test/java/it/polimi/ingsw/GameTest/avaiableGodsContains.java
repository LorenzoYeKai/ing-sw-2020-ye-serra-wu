package it.polimi.ingsw.GameTest;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;



import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class avaiableGodsContains {
    Game game;
    @BeforeEach
    void init(){
        List<String> names = List.of("player 1", "player 2","player 3");
        game = new Game(names);
        game.addAvailableGods(GodType.APOLLO);
        game.addAvailableGods(GodType.ARTEMIS);
        game.addAvailableGods(GodType.ATHENA);

    }
    @Test
    @DisplayName("Controllo lista")
    void avaiableGodsContains (){
        ArrayList prova = new ArrayList();
    }
}
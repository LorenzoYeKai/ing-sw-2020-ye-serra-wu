package it.polimi.ingsw;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.models.game.gods.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {

    Game game;
    GodType[] sample = {GodType.ATHENA, GodType.APOLLO};;

    @BeforeEach
    void twoPlayerGameCreation(){
        String[] names = {"peppino", "giuseppi"};
        game = new Game(2, names);
        game.setAvailableGods(sample);
    }

    @Test
    void choseCard(){
        game.listOfPlayers.stream()
                .forEach(player -> player.chooseCard());
    }

}

package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

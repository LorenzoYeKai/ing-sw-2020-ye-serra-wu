package it.polimi.ingsw.client;

import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.models.game.gods.God;

import java.io.Serializable;
import java.util.ArrayList;

public class GameSetupReply implements Serializable {

    private String name;
    private ArrayList<God> listOfAvaiableGods;
    private int index;



    public GameSetupReply(String name){
        this.name=name;

    }

    public String getName(){return this.name;}

    public void chooseAvaiableGods(int numberOfPlayer,ArrayList<God> allPossibleGods){

    }



    public ArrayList<God> getAvailableGods(){
        return listOfAvaiableGods;
    }

    public int getPlayerIndex() {
        return index;
    }

}

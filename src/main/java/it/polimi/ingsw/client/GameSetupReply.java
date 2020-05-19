package it.polimi.ingsw.client;

import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodType;

import java.io.Serializable;
import java.util.ArrayList;

public class GameSetupReply extends ClientReply implements Serializable  {

    private GodType[] listOfAvailableGods;
    private int index;



    public GameSetupReply(String name){
        super(name);

    }

    public GameSetupReply(GameSetupReply copy){
        super(copy.getClientName());
        this.listOfAvailableGods = copy.listOfAvailableGods;
        this.index = copy.index;
    }

    public void chooseAvaiableGods(int numberOfPlayer,ArrayList<God> allPossibleGods){

    }



    public GodType[] getAvailableGods(){
        return listOfAvailableGods;
    }

    public int getPlayerIndex() {
        return index;
    }

}

package it.polimi.ingsw.models.game.gods;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Contains all the Gods available in the game
 */
public enum GodType implements Serializable {


    APHRODITE,
    APOLLO,
    ARTEMIS,
    ATHENA,
    ATLAS,
    DEMETER,
    HEPHAESTUS,
    MINOTAUR,
    PAN,
    PROMETHEUS;

    private static final long serialVersionUID = 2L;

    /**
     * Parse the input
     */
    public static GodType parse(String s) {
        return GodType.valueOf(s.toUpperCase());
    }

    public static List<GodType> getListOfGods(){
        return Arrays.asList(GodType.values());
    }

    public static boolean contains(String s){
        List<String> allTypes = new ArrayList<>();
        for(int i = 0; i < GodType.getListOfGods().size(); i++){
            allTypes.add(GodType.getListOfGods().get(i).toString());
        }
        return allTypes.contains(s.toUpperCase());
    }

}

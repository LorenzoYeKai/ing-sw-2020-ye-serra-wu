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

    public static GodType parseFromGod(God god){
        if(god instanceof Apollo){
            return GodType.APOLLO;
        }
        if(god instanceof Athena){
            return GodType.ATHENA;
        }
        if(god instanceof Artemis){
            return GodType.ARTEMIS;
        }
        if(god instanceof Atlas){
            return GodType.ATLAS;
        }
        if(god instanceof Demeter){
            return GodType.DEMETER;
        }
        if(god instanceof Hephaestus){
            return GodType.HEPHAESTUS;
        }
        if(god instanceof Minotaur){
            return GodType.MINOTAUR;
        }
        if(god instanceof Pan){
            return GodType.PAN;
        }
        if(god instanceof Prometheus){
            return GodType.PROMETHEUS;
        }
        throw new IllegalArgumentException("ERROR");
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

package Project;

import java.util.*;

/**
 * 
 */
public class Space {

    /**
     * Default constructor
     */
    public Space() {
    }

    public int x;

    public int y;

    public Boolean occupiedByWorker;

    public Boolean occupiedByDome;

    public Boolean border;

    public int level;


    public int getLevel() {
        return level;
    }


    public void addLevel() {
        if(level==3) {
            System.out.println("Livello massimo, errore di mossa");
        }
        else {
            level=level+1;
        }

    }

    public Boolean isOccupied() {
        return occupiedByDome || occupiedByWorker;
    }


    public Boolean isBorder() {
        /**
         * Scegliere come farlo (se metterlo nel costruttore di world)
         */
        return null;

    }
    public Boolean isOccupiedByDome() {
        return occupiedByDome;
    }
    public Boolean isOccupiedByWorker() {
        return occupiedByWorker;
    }

}
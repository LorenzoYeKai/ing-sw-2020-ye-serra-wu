package it.polimi.ingsw.views.utils;

import java.io.Serializable;

public class Coordinates implements Serializable {

    private int x;
    private int y;

    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}

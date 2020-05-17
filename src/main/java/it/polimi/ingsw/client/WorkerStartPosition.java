package it.polimi.ingsw.client;

import java.io.Serializable;

public class WorkerStartPosition implements Serializable {
    private int[] xposition;
    private int[] yposition;
    private String name;

    public WorkerStartPosition() {

    }

    public String getName(){return this.name;}

    public int getX(int n){return xposition[n];}

    public int getY(int n){return yposition[n];}


}

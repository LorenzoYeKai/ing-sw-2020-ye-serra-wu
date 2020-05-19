package it.polimi.ingsw.client;

import java.io.Serializable;

public class WorkerSetupReply extends ClientReply implements Serializable {

    private int[] xPosition;
    private int[] yPosition;

    public WorkerSetupReply(String clientName) {
        super(clientName);

    }

    public int getX(int n){return xPosition[n];}

    public int getY(int n){return yPosition[n];}


}

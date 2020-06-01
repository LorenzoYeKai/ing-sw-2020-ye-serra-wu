package it.polimi.ingsw.client;

import java.io.Serializable;

public abstract class ClientReply implements Serializable {

    private String clientName;

    public ClientReply(String clientName){
        this.clientName = clientName;
    }


    public String getClientName(){
        return this.clientName;
    }
}

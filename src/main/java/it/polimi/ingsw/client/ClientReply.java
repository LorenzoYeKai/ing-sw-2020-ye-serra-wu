package it.polimi.ingsw.client;

import it.polimi.ingsw.Client;

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

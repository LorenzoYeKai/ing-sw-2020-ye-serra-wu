package it.polimi.ingsw.requests;

import it.polimi.ingsw.NotExecutedException;

import java.io.Serializable;

public interface RemoteRequestHandler {
    boolean isProcessable(Object input);
    Serializable processRequest(Object request) throws NotExecutedException;
}

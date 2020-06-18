package it.polimi.ingsw.rpc;

import it.polimi.ingsw.NotExecutedException;

import java.io.Serializable;

public interface RemoteCommandHandler {
    boolean isProcessable(Object command);
    Serializable processCommand(Object command) throws NotExecutedException;
}

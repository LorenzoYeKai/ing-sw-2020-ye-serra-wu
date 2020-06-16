package it.polimi.ingsw.rpc;

import it.polimi.ingsw.controller.NotExecutedException;

import java.io.IOException;
import java.io.Serializable;

class Response implements Serializable {
    private final long sequenceNumber;
    private final boolean isException;
    private final Serializable result;

    public Response(long sequenceNumber, boolean isException, Serializable result) {
        this.sequenceNumber = sequenceNumber;
        this.isException = isException;
        this.result = result;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public Serializable getResult() throws NotExecutedException, IOException {
        if (isException) {
            if (result instanceof NotExecutedException) {
                throw (NotExecutedException) result;
            } else {
                // remote peer's handler has failed with a possibly
                // unrecoverable runtime exception, from perspective
                // of local caller, remote peer will die soon.
                // So I think here it's appropriate to throw an IOException
                // and let the calling code handle it just like a network
                // failure.
                throw new IOException((RuntimeException)result);
            }
        }
        return result;
    }
}

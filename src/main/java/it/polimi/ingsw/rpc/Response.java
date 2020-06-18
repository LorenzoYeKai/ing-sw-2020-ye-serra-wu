package it.polimi.ingsw.rpc;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.InternalError;

import java.io.Serializable;

/**
 * @see RequestProcessor
 * @see Request
 */
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

    public Serializable getResult() throws NotExecutedException {
        if (isException) {
            if (result instanceof NotExecutedException) {
                throw (NotExecutedException) result;
            } else {
                throw new InternalError("Unexpected exception type " + result);
            }
        }
        return result;
    }
}

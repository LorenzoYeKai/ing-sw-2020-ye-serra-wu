package it.polimi.ingsw.rpc;

import it.polimi.ingsw.models.InternalError;

import java.io.Serializable;

/**
 * Represents a serializable request which wraps a command.
 * It might be a request that needs a response from the peer
 * (with a {@link #sequenceNumber})
 * Or it might be a notification (with {@link #sequenceNumber} same as
 * {@link #noNeedToReply}) that does not need to be replied.
 */
class Request implements Serializable {
    static final long noNeedToReply = -1;
    private final long sequenceNumber;
    private final Serializable command;

    /**
     * Creates a {@link Request} that needs to be replied with a {@link Response}.
     *
     * @param sequenceNumber A number associated with the request-response pair
     * @param command        The command of this request, which will be handled by
     *                       receiver's handlers.
     */
    Request(long sequenceNumber, Serializable command) {
        if (sequenceNumber == Request.noNeedToReply) {
            throw new InternalError("This sequence number is a special value");
        }
        this.sequenceNumber = sequenceNumber;
        this.command = command;
    }

    /**
     * Creates a {@link Request} that does not need to be replied with a
     * {@link Response}, i.e. the caller does not care if the request succeeded
     * or if it generates any result.
     *
     * @param command The command of this request, which might be handled by
     *                receiver's handlers.
     */
    public Request(Serializable command) {
        this.sequenceNumber = Request.noNeedToReply;
        this.command = command;
    }

    /**
     * Checks whether the current request needs to be replied with a
     * {@link Response}.
     *
     * @return true if this request needs to be replied.
     */
    public boolean needReply() {
        return this.sequenceNumber != Request.noNeedToReply;
    }

    /**
     * Get the command contained in the current request.
     *
     * @return the command contained in the current request.
     */
    public Serializable getCommand() {
        return command;
    }

    /**
     * Generates a {@link Response} associated with the current request.
     *
     * @param result the result of this request
     * @return the generated {@link Response}
     */
    public Response replyResult(Serializable result) {
        if (!this.needReply()) {
            throw new InternalError("this request doesn't need to be replied");
        }
        return new Response(this.sequenceNumber, false, result);
    }

    /**
     * Generates a {@link Response} associated with the current request.
     *
     * @param exception the error occurred when processing the request.
     * @return the generated {@link Response}
     */
    public Response replyError(Exception exception) {
        if (!this.needReply()) {
            throw new InternalError("this request doesn't need to be replied");
        }
        return new Response(this.sequenceNumber, true, exception);
    }

}

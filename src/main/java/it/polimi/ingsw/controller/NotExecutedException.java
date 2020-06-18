package it.polimi.ingsw.controller;

/**
 * Represents an exception which should be thrown by the controller or other
 * code when an action requested by the user is not executed (for example,
 * because rules does not allow it). Controller should check everything and
 * throw this exception before making changes to the current state.
 * Thus, this should be a non-critical error. Even if this exception has been
 * thrown, the state of application should still remain valid.
 */
public class NotExecutedException extends Exception {
    public NotExecutedException(String why) {
        super(why);
    }
}

package it.polimi.ingsw.models;

import it.polimi.ingsw.controller.NotExecutedException;

/**
 * An exception thrown by Model or other code in exceptional situations.
 * It shouldn't be caught or handled, because the internal state of Model might
 * already be corrupted so it might be impossible to recover.
 * Instead, they should be discovered during test phase, and changes to the
 * application logic should be made to prevent these exceptions from happening.
 *
 * For example, Controller should validate all input
 * (and throw {@link NotExecutedException} on errors,
 * which is recoverable) before making calls to the Model.
 */
public class InternalError extends RuntimeException {
    public InternalError(String why) {
        super(why);
    }
    public InternalError(Exception innerException) {
        super(innerException);
    }
}

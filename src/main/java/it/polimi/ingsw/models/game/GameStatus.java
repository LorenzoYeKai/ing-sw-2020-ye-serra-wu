package it.polimi.ingsw.models.game;

/**
 *
 *
 * This enumeration allows me to know what stage of the game we are in.
 * We use this enumeration to distinguish which methods to call when creating the game
 */
public enum GameStatus {
    PLAYER_JOINING,
    SETUP,
    CHOOSING_GODS,
    PLACING,
    PLAYING,
    ENDED,
}

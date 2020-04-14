package it.polimi.ingsw.models.game.gods;

/**
 * Contains all the Gods available in the game
 */
public enum GodType {
    APHRODITE,
    APOLLO,
    ARTEMIS,
    ATHENA,
    ATLAS,
    DEMETER,
    HEPHAESTUS,
    MINOTAUR,
    PAN,
    PROMETHEUS;

    /**
     * Parse the input
     */
    public static GodType parse(String s) {
        return GodType.valueOf(s.toUpperCase());
    }
}

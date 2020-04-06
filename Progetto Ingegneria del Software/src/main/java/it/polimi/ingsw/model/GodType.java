package it.polimi.ingsw.model;

/**
 * Contains all the Gods available in the game
 */
public enum GodType {
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

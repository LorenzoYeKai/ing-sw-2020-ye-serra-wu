package it.polimi.ingsw;

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

    public static GodType parse(String s) {
        return GodType.valueOf(s.toUpperCase());
    }
}

package it.polimi.ingsw.models.game.gods;

import java.io.Serializable;

/**
 * Creates God with the correct dynamic type
 */
public class GodFactory implements Serializable {

    public God getGod(GodType god){
        return switch (god) {
            case APOLLO -> new Apollo();
            case ARTEMIS -> new Artemis();
            case ATHENA -> new Athena();
            case ATLAS -> new Atlas();
            case DEMETER -> new Demeter();
            case HEPHAESTUS -> new Hephaestus();
            case MINOTAUR -> new Minotaur();
            case PAN -> new Pan();
            case PROMETHEUS -> new Prometheus();
        };
    }
}

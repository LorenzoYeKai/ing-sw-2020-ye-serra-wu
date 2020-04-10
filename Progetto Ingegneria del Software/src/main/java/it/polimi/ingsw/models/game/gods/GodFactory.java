package it.polimi.ingsw.models.game.gods;

/**
 * Creates God with the correct dynamic type
 */
public class GodFactory {

    public God getGod(GodType god){
        God w = null;
        switch (god){
            case APOLLO:
                w = new Apollo();
                break;
            case ARTEMIS:
                w = new Artemis();
                break;
            case ATHENA:
                w = new Athena();
                break;
            case ATLAS:
                w = new Atlas();
                break;
            case DEMETER:
                w = new Demeter();
                break;
            case HEPHAESTUS:
                w = new Hephaestus();
                break;
            case MINOTAUR:
                w = new Minotaur();
                break;
            case PAN:
                w = new Pan();
                break;
            case PROMETHEUS:
                w = new Prometheus();
                break;
        }
        return w;
    }
}
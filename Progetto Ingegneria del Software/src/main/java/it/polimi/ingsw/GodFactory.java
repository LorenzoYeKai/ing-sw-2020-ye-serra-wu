package it.polimi.ingsw;

public class GodFactory {

    public God getGod(GodType god, ActualRule rules){
        God w = null;
        switch (god){
            case APOLLO:
                w = new Apollo(rules);
                break;
            case ARTEMIS:
                w = new Artemis(rules);
                break;
            case ATHENA:
                w = new Athena(rules);
                break;
            case ATLAS:
                w = new Atlas(rules);
                break;
            case DEMETER:
                w = new Demeter(rules);
                break;
            case HEPHAESTUS:
                w = new Hephaestus(rules);
                break;
            case MINOTAUR:
                w = new Minotaur(rules);
                break;
            case PAN:
                w = new Pan(rules);
                break;
            case PROMETHEUS:
                w = new Prometheus(rules);
                break;
        }
        return w;
    }
}

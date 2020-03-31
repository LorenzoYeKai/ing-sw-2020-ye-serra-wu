package it.polimi.ingsw;

public class WorkerFactory {

    public Worker getWorker(GodType god, Player player){
        Worker w = null;
        switch (god){
            case APOLLO:
                w = new Apollo(player);
                break;
            case ARTEMIS:
                w = new Artemis(player);
                break;
            case ATHENA:
                w = new Athena(player);
                break;
            case ATLAS:
                w = new Atlas(player);
                break;
            case DEMETER:
                w = new Demeter(player);
                break;
            case HEPHAESTUS:
                w = new Hephaestus(player);
                break;
            case MINOTAUR:
                w = new Minotaur(player);
                break;
            case PAN:
                w = new Pan(player);
                break;
            case PROMETHEUS:
                w = new Prometheus(player);
                break;
        }
        return w;
    }
}

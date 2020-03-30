package it.polimi.ingsw;

public class WorkerFactory {

    public Worker getWorker(GodType god, Player player, World world){
        Worker w = null;
        switch (god){
            case APOLLO:
                w = new Apollo(player, world);
                break;
            case ARTEMIS:
                w = new Artemis(player, world);
                break;
            case ATHENA:
                w = new Athena(player, world);
                break;
            case ATLAS:
                w = new Atlas(player, world);
                break;
            case DEMETER:
                w = new Demeter(player, world);
                break;
            case HEPHAESTUS:
                w = new Hephaestus(player, world);
                break;
            case MINOTAUR:
                w = new Minotaur(player, world);
                break;
            case PAN:
                w = new Pan(player, world);
                break;
            case PROMETHEUS:
                w = new Prometheus(player, world);
                break;
        }
        return w;
    }
}

package it.polimi.ingsw;

public class Space {
    public int x;

    public int y;

    public boolean occupiedByWorker;

    public boolean occupiedByDome;

    public boolean border;

    public int level;

    public Space() {
        this.occupiedByWorker=false;
        this.occupiedByDome=false;
        this.level=0;
    }

    public int getLevel(){
        return level;
    }

    public void adaLevel(){
        level=level+1;
    }

    public Boolean isOccupiedByWorker() {
        return occupiedByWorker;
    }
    public Boolean isBorder(){
        /* scegliere come implementarlo funzione aggiuntiva */
    }

    public Boolean isOccupiedByDome(){
        return occupiedByDome;
    }

    public Boolean isOccupied(){
        return (occupiedByDome || occupiedByWorker)
    }



}

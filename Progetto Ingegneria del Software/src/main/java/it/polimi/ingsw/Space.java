package it.polimi.ingsw;

public class Space {
    public int x;

    public int y;

    public boolean occupiedByWorker;

    public boolean occupiedByDome;

    public boolean border;

    public int level;

    public Space(int coordiantay,int coordinatax) {
        this.x=coordinatax;
        this.y=coordiantay;
        this.occupiedByWorker=false;
        this.occupiedByDome=false;
        this.level=0;
    }

    public int getLevel(){
        return level;
    }

    public void addLevel(){
        level=level+1;
    }

    public boolean isOccupiedByWorker() {
        return occupiedByWorker;
    }

    public boolean isBorder(){
        /* scegliere come implementarlo funzione aggiuntiva */
    }

    public boolean isOccupiedByDome(){
        return occupiedByDome;
    }

    public boolean isOccupied(){
        return (occupiedByDome || occupiedByWorker);
    }



}

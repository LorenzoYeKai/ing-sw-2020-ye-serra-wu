package it.polimi.ingsw;

public class Space {
    public int x;

    public int y;

    public boolean occupiedByWorker;

    public boolean occupiedByDome;

    public boolean border;

    public int level;

    public Space(int y,int x) {
        this.x = x;
        this.y = y;
        this.occupiedByWorker=false;
        this.occupiedByDome=false;
        this.level=0;
    }

    public int getLevel(){
        return level;
    }

    public void addLevel(){
        if(this.level < 3) { //Prevent level > 3
            level++;
        }
        else{
            System.out.println("You cannot build any further!");
        }
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

package it.polimi.ingsw.model;

public class Space {
    private int x;

    private int y;

    private Worker worker;

    private boolean occupiedByDome;

    //public boolean border;

    private int level;

    public Space(int y, int x) {
        this.x = x;
        this.y = y;
        this.worker = null;
        this.occupiedByDome = false;
        this.level = 0;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Adds a level to the space when a worker builds on it
     * Cannot be taller than 3
     */
    public void addLevel() {
        if (this.level < 3) { //Prevent level > 3
            level++;
        } else {
            System.out.println("You cannot build any further!");
        }
    }

    public boolean isOccupiedByWorker() {
        return this.worker != null;
    }

    /*public boolean isBorder(){
    }*/

    public boolean isOccupiedByDome() {
        return occupiedByDome;
    }

    public boolean isOccupied() {
        return (occupiedByDome || this.worker != null);
    }

    /**
     * If a dome is set, the space is occupied by a dome
     */
    public void setDome() {
        this.occupiedByDome = true;
    }

    public void setWorker(Worker worker){
        this.worker = worker;
    }

    public void removeWorker(){
        this.worker = null;
    }
}


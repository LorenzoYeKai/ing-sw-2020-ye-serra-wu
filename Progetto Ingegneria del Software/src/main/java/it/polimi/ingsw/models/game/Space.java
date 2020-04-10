package it.polimi.ingsw.models.game;

public class Space implements SpaceData {
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

    @Override
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

    @Override
    public WorkerData getWorker() {
        return this.worker;
    }

    @Override
    public boolean isOccupiedByDome() {
        return occupiedByDome;
    }

    @Override
    public int getX(){
        return this.x;
    }

    @Override
    public int getY(){
        return this.y;
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

    /**
     * Checks if the given coordinates are of a space within the world
     */
    public boolean isInWorld()
    {
        return this.y > -1 && this.y < 5 && this.x > -1 && this.x < 5;
    }

    /**
     * Calculates the level difference between 2 spaced
     * Returns negative if moving up
     * Returns positive if moving down
     * Returns 0 if moving in same level
     */
    public  int levelDifference(Space space){
        return this.level - space.getLevel();
    }

    /**
     * Checks if a space is a neighbor of another space
     */
    public boolean isNeighbor(Space space)
    {
        return (this.x == space.x - 1 && this.y == space.y) || (this.x == space.x + 1 && this.y == space.y) ||
                (this.x == space.x && this.y == space.y - 1) || (this.x == space.x && this.y == space.y + 1) ||
                (this.x == space.x + 1 && this.y == space.y + 1) || (this.x == space.x - 1 && this.y == space.y + 1) ||
                (this.x == space.x + 1 && this.y == space.y - 1) || (this.x == space.x - 1 && this.y == space.y - 1);
    }
}

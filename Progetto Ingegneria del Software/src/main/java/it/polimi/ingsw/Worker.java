package it.polimi.ingsw;


/**
 * Implementation of the basic turn without God powers
 */

abstract class Worker {
    private int x;
    private int y;

    public Worker(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void move(int x, int y){
        if(World.canMoveThere(this.x, this.y, x, y)){ //Need a method that checks if i can move in a space
            victory(x, y);
            this.x = x;
            this.y = y;
        }
        else{
            System.out.println("You cannot move there!");
        }
    }

    public void build(int x, int y){
        if(World.canBuildThere(this.x, this.y, x, y)){ //Need a method that checks if i can build in a space
            if(World.getSpaces(x, y).getLevel() == 3){ //Need a method that returns the space on the world given the coordinates
                World.getSpacec(x, y).setDome();
            }
            else{ //level > 3 cannot exist due to previous control
                World.getSpacec(x, y).addLevel();
            }
        }
    }

    public void victory(int x, int y){
        if(this.x != x || this.y != y){
            if(World.getSpaces(x, y).getLevel() == 3 && World.getSpaces(this.x, this.y).getLevel() != 3){ //Need a method that returns the space on the world given the coordinates
                this.x = x;
                this.y = y;
                Game.endGame();
            }
        }
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setY(int n) {
        this.y = n;
    }

    public void setX(int n) {
        this.x = n;
    }

    abstract void printPosition();
}

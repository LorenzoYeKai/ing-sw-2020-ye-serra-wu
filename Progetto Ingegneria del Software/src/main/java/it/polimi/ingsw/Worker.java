package it.polimi.ingsw;

abstract class Worker {
    private int x;
    private int y;

    public Worker(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void move(int x, int y){
        if(World.canMoveThere(this.x, this.y, x, y)){
            this.x = x;
            this.y = y;
        }
    }

    public void build(int x, int y){

    }

    public void victory(int x, int y){
        if(this.x != x || this.y != y){
            if(World.getSpaces(x, y).getLevel() == 3 && World.getSpaces(this.x, this.y).getLevel() != 3){

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

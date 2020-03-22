package it.polimi.ingsw;

public class Apollo extends Worker {

    public Apollo(int x, int y){
        super(x, y);
    }

    public void printPosition(){
        System.out.println("X: " + this.getX());
        System.out.println("Y: " + this.getY());
    }
}

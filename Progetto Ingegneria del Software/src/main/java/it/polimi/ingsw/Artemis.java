package it.polimi.ingsw;

import java.util.Scanner;

public class Artemis extends Worker{

    public Artemis(int x, int y){
        super(x, y);
    }

    public void startTurn(){
        boolean movedTwice = false;
        while(true){ //Move loop
            System.out.println("Where should your worker move?");
            Scanner coordinates = new Scanner(System.in);
            int x = coordinates.nextInt();
            int y = coordinates.nextInt();
            if(World.canMoveThere(this.getX(), this.getY(), x, y)) {
                System.out.println("Your worker moved form " + "[" + this.getX() + "][" + this.getY() + "] to " + "[" + x + "][" + y + "].");
                move(x, y);
                if(!movedTwice) { //Input control not implemented yet
                    System.out.println("Do you want to move your worker again? y/n");
                    Scanner scanner = new Scanner(System.in);
                    String choice = scanner.nextLine();
                    if (choice.toLowerCase().equals("n")) {
                        break;
                    }
                    movedTwice = true;
                }
                else{
                    break;
                }
            }
            else{
                System.out.println("You cannot move there!");
            }
        }


    }
}

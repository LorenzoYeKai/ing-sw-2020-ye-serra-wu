package it.polimi.ingsw;

import java.util.Scanner;

public class Artemis extends Worker{

    public Artemis(int x, int y){
        super(x, y);
    }

    @Override
    public void startTurn(){
        int originalX = this.getX();
        int originalY = this.getY();
        move(); //First movement
        while(true){ //input control
            System.out.println("Do you want to move your worker again? y/n");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if(input.toLowerCase().equals("y")){
                secondMovement(originalX, originalY); //The worker MAY move 2 times in a turn
                break;
            }
            else if(input.toLowerCase().equals("n")) break;
            else{
                System.out.println("Invalid input!");
            }
        }
        build();
    }

    private void secondMovement(int originalX, int originalY){ //Temporary second movement implementation
        while (true) { //Move loop (input control)
            System.out.println("Where should your worker move?");
            Scanner coordinates = new Scanner(System.in);
            int x = coordinates.nextInt();
            int y = coordinates.nextInt();
            if (World.canMoveThere(this.getX(), this.getY(), x, y) && x != originalX && y  != originalY) { //Check coordinates validity (cannot move back to the original position)
                System.out.println("Your worker moved form " + "[" + this.getX() + "][" + this.getY() + "] to " + "[" + x + "][" + y + "].");
                victory(x, y); //Check win condition
                setX(x);
                setY(y);
                break;
            }
            System.out.println("You cannot move there!");
        }
    }
}

package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.Worker;

import java.util.Scanner;

public class Artemis extends God{

    /**
     * Can move 2 times (not to the original space) in a single turn
     */
    @Override
    public void performActions(Worker worker){
        int originalX = worker.getX();
        int originalY = worker.getY();
        perform(worker::move, "move");
        performSecondMovement(worker, originalX, originalY);
        perform(worker::build, "build");
    }

    /**
     * The condition (originalX != x || originalY != y) is added to the active rules
     * @param originalX : x coordinate of the original space
     * @param originalY : y coordinate of the original space
     */
    private void secondMovement(Worker worker, int originalX, int originalY, int x , int y){ //Temporary second movement implementation
        if (worker.getRules().canMoveThere(worker.getX(), worker.getY(), x, y) && (originalX != x || originalY != y)) { //Check coordinates validity
            System.out.println("Your worker moved form " + "[" + worker.getX() + "][" + worker.getY() + "] to " + "[" + x + "][" + y + "].");
            worker.victory(x, y); //Check win condition
            worker.getWorld().getSpaces(worker.getX(), worker.getY()).removeWorker();
            worker.setPosition(x, y);
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Wrapper
     */
    private void performSecondMovement(Worker worker, int originalX, int originalY){
        System.out.println("Do you want to move again? y/n");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        if(answer.toLowerCase().equals("y")){
            System.out.println("Where should your worker move?");
            Scanner moveCoordinates = new Scanner(System.in);
            int x = moveCoordinates.nextInt();
            int y = moveCoordinates.nextInt();
            while(true){
                try{
                    secondMovement(worker, originalX, originalY, x, y);
                    break;
                }
                catch (IllegalArgumentException e){
                    System.out.println("You cannot move there!");
                    System.out.println("Choose another space.");
                    x = moveCoordinates.nextInt();
                    y = moveCoordinates.nextInt();
                }
            }
        }
    }


}

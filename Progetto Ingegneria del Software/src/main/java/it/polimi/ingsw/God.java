package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class God {

    private ActualRule rules;


    public God(ActualRule rules){
        this.rules = rules;
    }

    public void move(Worker worker) { //Movable spaces display not implemented yet
        int currentX = worker.getX();
        int currentY = worker.getY();
        System.out.println("Where should your worker move?");
        Scanner coordinates = new Scanner(System.in);
        while (true) { //Move loop (input control)
            int x = coordinates.nextInt();
            int y = coordinates.nextInt();
            if (rules.canMoveThere(currentX, currentY, x, y)) { //Check coordinates validity
                System.out.println("Your worker moved form " + "[" + currentX + "][" + currentY + "] to " + "[" + x + "][" + y + "].");
                victory(x, y, worker); //Check win condition
                worker.setPosition(x, y);
                break;
            }
            System.out.println("You cannot move there!");
        }
    }

    public void build (Worker worker){ //Buildable spaces display not implemented yet
        System.out.println("Where should your worker build?");
        Scanner coordinates = new Scanner(System.in);
        int x = coordinates.nextInt();
        int y = coordinates.nextInt();
        while (true) { //Move loop (input control)
            if (rules.canBuildThere(worker.getX(), worker.getY(), x, y)) { //Check coordinates validity
                if (worker.getWorld().getSpaces(x, y).getLevel() == 3) {
                    worker.getWorld().getSpaces(x, y).setDome();
                    System.out.println("Your worker built a dome in " + "[" + x + "][" + y + "].");
                } else { //level > 3 cannot exist due to previous control
                    worker.getWorld().getSpaces(x, y).addLevel();
                    System.out.println("Your worker built a level " + worker.getWorld().getSpaces(x, y).getLevel() + " block in " + "[" + x + "][" + y + "].");
                }
                break;
            }
            System.out.println("You cannot build there!");
            System.out.println("Where should your worker build?");
            x = coordinates.nextInt();
            y = coordinates.nextInt();
        }
    }

    public void victory ( int x, int y, Worker worker){ //This method is called only after checking that the worker can move to that position
        if (worker.getWorld().getSpaces(x, y).getLevel() == 3 && worker.getWorld().getSpaces(worker.getX(), worker.getY()).getLevel() != 3) {
            worker.setPosition(x, y);
            worker.getPlayer().game.endGame(); //If true the game ends
        }
    }

    public ActualRule getRules(){
        return this.rules;
    }

}

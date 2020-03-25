package it.polimi.ingsw;


import java.util.Scanner;

/**
 * Implementation of the basic turn without God powers
 */

abstract class Worker {
    private int x;
    private int y;
    private final Player player;

    public Worker(Player player) {
        this.player = player;
    }

    public void startTurn(){
        move();
        build();
    }

    public void move() { //Movable spaces display not implemented yet
        System.out.println("Where should your worker move?");
        Scanner coordinates = new Scanner(System.in);
        while (true) { //Move loop (input control)
            int x = coordinates.nextInt();
            int y = coordinates.nextInt();
            if (World.canMoveThere(this.x, this.y, x, y)) { //Check coordinates validity
                System.out.println("Your worker moved form " + "[" + this.x + "][" + this.y + "] to " + "[" + x + "][" + y + "].");
                victory(x, y); //Check win condition
                this.x = x;
                this.y = y;
                break;
            }
            System.out.println("You cannot move there!");
        }
    }

        public void build (){ //Buildable spaces display not implemented yet
            System.out.println("Where should your worker build?");
            Scanner coordinates = new Scanner(System.in);
            while (true) { //Move loop (input control)
                int x = coordinates.nextInt();
                int y = coordinates.nextInt();
                if (World.canBuildThere(this.x, this.y, x, y)) { //Check coordinates validity
                    if (World.getSpaces(x, y).getLevel() == 3) {
                        World.getSpaces(x, y).setDome(); //Needs a method for setting the dome
                    } else { //level > 3 cannot exist due to previous control
                        World.getSpaces(x, y).addLevel();
                    }
                    break;
                }
                System.out.println("You cannot build there!");
            }
        }

        public void victory ( int x, int y){ //This method is called only after checking that the worker can move to that position
            if (World.getSpaces(x, y).getLevel() == 3 && World.getSpaces(this.x, this.y).getLevel() != 3) {
                this.x = x;
                this.y = y;
                Game.endGame(); //If true the game ends
            }
        }

        public int getY () {
            return y;
        }

        public int getX () {
            return x;
        }

        public void setY ( int n){
            this.y = n;
        }

        public void setX ( int n){
            this.x = n;
        }

        public void setPosition(int x, int y){
            this.x = x;
            this.y = y;
        }

        //abstract void printPosition();
}


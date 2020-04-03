package it.polimi.ingsw;


import java.util.Scanner;

/**
 * Implementation of the basic turn without God powers
 */

public class Worker {
    private int x;
    private int y;
    private final Player player;
    private final World world;
    private God god;

    public Worker(Player player) {
        this.player = player;
        this.world = this.player.game.getWorld();
    }

    public void startTurn(){

        god.move(this);
        god.build(this);
    }

    /*public void move() { //Movable spaces display not implemented yet
        System.out.println("Where should your worker move?");
        Scanner coordinates = new Scanner(System.in);
        while (true) { //Move loop (input control)
            int x = coordinates.nextInt();
            int y = coordinates.nextInt();
            if (world.canMoveThere(this.x, this.y, x, y)) { //Check coordinates validity
                System.out.println("Your worker moved form " + "[" + this.x + "][" + this.y + "] to " + "[" + x + "][" + y + "].");
                victory(x, y); //Check win condition
                this.x = x;
                this.y = y;
                break;
            }
            System.out.println("You cannot move there!");
        }
    }*/

        /*public void build (){ //Buildable spaces display not implemented yet
            System.out.println("Where should your worker build?");
            Scanner coordinates = new Scanner(System.in);
            int x = coordinates.nextInt();
            int y = coordinates.nextInt();
            while (true) { //Move loop (input control)
                if (world.canBuildThere(this.getX(), this.getY(), x, y)) { //Check coordinates validity
                    if (world.getSpaces(x, y).getLevel() == 3) {
                        world.getSpaces(x, y).setDome();
                        System.out.println("Your worker built a dome in " + "[" + x + "][" + y + "].");
                    } else { //level > 3 cannot exist due to previous control
                        world.getSpaces(x, y).addLevel();
                        System.out.println("Your worker built a level " + world.getSpaces(x, y).getLevel() + " block in " + "[" + x + "][" + y + "].");
                    }
                    break;
                }
                System.out.println("You cannot build there!");
                System.out.println("Where should your worker build?");
                x = coordinates.nextInt();
                y = coordinates.nextInt();
            }
        }*/

        /*public void victory ( int x, int y){ //This method is called only after checking that the worker can move to that position
            if (world.getSpaces(x, y).getLevel() == 3 && world.getSpaces(this.x, this.y).getLevel() != 3) {
                this.x = x;
                this.y = y;
                this.player.game.endGame(); //If true the game ends
            }
        }*/

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

        public Player getPlayer(){
        return this.player;
        }

        public World getWorld(){
        return this.world;
        }

        //abstract void printPosition();
}


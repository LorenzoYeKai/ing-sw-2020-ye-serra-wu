package it.polimi.ingsw;

import java.util.Scanner;

public class Atlas extends Worker {

    public Atlas(Player player){
        super(player);
    }

    @Override
    public void build(){
        System.out.println("Where should your worker build?");
        Scanner coordinates = new Scanner(System.in);
        while (true) { //Move loop (input control)
            int x = coordinates.nextInt();
            int y = coordinates.nextInt();
            if (this.getWorld().canBuildThere(this.getX(), this.getY(), x, y)) { //Check coordinates validity
                if(this.getWorld().getSpaces(x, y).getLevel() == 3){
                    this.getWorld().getSpaces(x, y).setDome();
                }
                else {
                    System.out.println("Do you want to build a dome? y/n");
                    Scanner scanner = new Scanner(System.in);
                    while (true) { //input control
                        String input = scanner.nextLine();
                        if (input.toLowerCase().equals("y")) {
                            this.getWorld().getSpaces(x, y).setDome();
                            break;
                        } else if (input.toLowerCase().equals("n")) {
                            this.getWorld().getSpaces(x, y).addLevel();
                            break;
                        }
                        else {
                            System.out.println("Invalid input!");
                        }
                    }
                    break;
                }
            }
            System.out.println("You cannot build there!");
        }
    }
}

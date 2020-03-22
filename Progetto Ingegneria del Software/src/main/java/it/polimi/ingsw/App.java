package it.polimi.ingsw;

import java.util.Scanner;

/**
 * Ciao
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Worker god;
        System.out.println("Choose the initial position:");
        Scanner scanner = new Scanner(System.in);
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        System.out.println(x + " " + y);
        god = new Apollo(x, y);
        //god.printPosition();
    }
}

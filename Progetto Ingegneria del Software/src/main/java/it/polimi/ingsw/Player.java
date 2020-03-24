package it.polimi.ingsw;

import java.util.Scanner;

public class Player {

    private String name;

    private Player nextPlayer;

    private boolean defeat;

    static Worker[] workers = new Worker[2];

    public Player() {
        setName();
        chooseCard();
        setWorkers();
    }


    public String getName() {
        return this.name;
    }

    private void setName() {
        System.out.println("Choose a name");
        Scanner Tastiera = new Scanner(System.in);
        this.name = Tastiera.nextLine();
    }

    private void chooseCard() {
        System.out.println("Choose a card");
        Scanner Tastiera = new Scanner(System.in);
        God temponary = Tastiera.nextLine();
        while (!Game.isAvaiableGod(temponary)) {   // mi serve un metodo per vedere se il potere scelto è fra quelli possbili
            System.out.println("error");
            System.out.println("Choose a god");
            God temponary = Tastiera.nextLine();
        }
        this.god = temponary;
    }

    public void setWorkers() {

        for (int i = 0; i < 2; i = i + 1) {
            System.out.println("Choose  Worker position");
            Scanner Tastiera = new Scanner(System.in);
            int tempx = Tastiera.nextInt();
            int tempy = Tastiera.nextInt();
            while (World.getSpaces(tempx, tempy).isOccupied() || !World.isInWorld(tempx, tempy)) {
                System.out.println("error,Choose Worker position");
                tempx = Tastiera.nextInt();
                tempy = Tastiera.nextInt();
                World.getSpaces(tempx, tempy).setOccupiedByWorker();
            }
            workers[i] = new Worker(tempx, tempy);
        }
    }

    public Worker selectWorker(int k) {
        return workers[k];
    }

    public God getGod() {
        return this.god;
    }

    public Boolean isDefeat() {
        return (!World.canMove(selectWorker(0).getX(), selectWorker(1).getY()));
    }
}



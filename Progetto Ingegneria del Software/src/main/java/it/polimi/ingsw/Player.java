package it.polimi.ingsw;

import java.util.Scanner;

public class Player {

    private String name;

    private Player nextPlayer;

    private boolean defeat = false;

    private final Worker[] workers = new Worker[2];

    private WorkerFactory factory = new WorkerFactory();

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
        Scanner scanner = new Scanner(System.in);
        this.name = scanner.nextLine();
    }

    private void chooseCard() {
        System.out.println("Choose a card");
        Scanner scanner = new Scanner(System.in);
        String god = scanner.nextLine();
        while (!Game.isAvaiableGod(god.toUpperCase())) {   // mi serve un metodo per vedere se il potere scelto è fra quelli possbili
            System.out.println("error");
            System.out.println("Choose a god");
            god = scanner.nextLine();
        }
        this.workers[0] = factory.getWorker(god, this);
        this.workers[1] = factory.getWorker(god, this);
    }

    public void setWorkers() {

        for (int i = 0; i < 2; i = i + 1) {
            System.out.println("Choose  Worker position");
            Scanner scanner = new Scanner(System.in);
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            while (World.getSpaces(x, y).isOccupied() || !World.isInWorld(x, y)) {
                System.out.println("error,Choose Worker position");
                x = scanner.nextInt();
                y = scanner.nextInt();
                World.getSpaces(x, y).setOccupiedByWorker();
            }
            workers[i].setPosition(x, y);
        }
    }

    public Worker selectWorker(int k) {
        return workers[k];
    }

    public Boolean isDefeat() {
        return (!World.canMove(selectWorker(0).getX(), selectWorker(1).getY()));
    }
}



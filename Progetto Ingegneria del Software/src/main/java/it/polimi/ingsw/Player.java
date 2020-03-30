package it.polimi.ingsw;

import java.util.Scanner;

public class Player {

    private String name;

    private Player nextPlayer;

    private boolean defeat = false;

    private final Worker[] workers = new Worker[2];

    public final Game game;

    public Player(Game game) {
        this.game = game;
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
        Worker[] workers = null;
        while(true) {
            try {
                GodType god = GodType.parse(scanner.nextLine());
                if(this.game.isGodAvailable(god)) {
                    System.out.println("God unavailable or already selected by other players");
                }
                workers = this.game.chooseGodAndGetWorkers(god, this);
                break;
            }
            catch(Exception e) {
                System.out.println("Error");
                System.out.println("Choose card again");
                continue;
            }
        }

        this.workers[0] = workers[0];
        this.workers[1] = workers[1];
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



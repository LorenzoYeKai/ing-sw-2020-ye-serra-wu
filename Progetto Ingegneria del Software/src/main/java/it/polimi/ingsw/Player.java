package it.polimi.ingsw;

import java.util.Scanner;

public class Player {

    private String name;

    private Player nextPlayer;

    private boolean defeat = false;

    private final Worker[] workers = new Worker[2];

    private God god;

    public final Game game;

    private int ruleIndex;

    public Player(Game game, int ruleIndex) { //Nella creazione dei player saranno assegnati i rule index in modo crescente
        this.game = game;
        this.ruleIndex = ruleIndex;
        setName();
        chooseCard();
        setWorkers();
    }

    public String getName() {
        return this.name;
    }

    private void setName() { //Non servirà più visto che il nome si sceglie quando si entra nella lobby dove i player non sono ancora costruiti
        System.out.println("Choose a name");
        Scanner scanner = new Scanner(System.in);
        this.name = scanner.nextLine();
    }

    private void chooseCard() {
        System.out.println("Choose a card");
        Scanner scanner = new Scanner(System.in);
        while(true) {
            try {
                GodType god = GodType.parse(scanner.nextLine());
                if(this.game.isGodAvailable(god)) {
                    this.god = this.game.chooseGod(god, this);
                    break;
                }
                System.out.println("God unavailable or already selected by other players");
            }
            catch(Exception e) {
                System.out.println("Error");
                System.out.println("Choose card again");
            }
        }
    }

    public void setWorkers() {

        for (int i = 0; i < 2; i = i + 1) {
            System.out.println("Choose  Worker position");
            Scanner scanner = new Scanner(System.in);
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            while (this.game.getWorld().getSpaces(x, y).isOccupied() || !this.game.getWorld().isInWorld(x, y)) {
                System.out.println("error,Choose Worker position");
                x = scanner.nextInt();
                y = scanner.nextInt();
                this.game.getWorld().getSpaces(x, y).setOccupiedByWorker();
            }
            workers[i] = new Worker(this);
            workers[i].setPosition(x, y);
        }
    }

    public Worker selectWorker(int k) {
        return workers[k];
    }

    public boolean isDefeated() {
        return (!this.game.getRules().canMove(selectWorker(0).getX(), selectWorker(1).getY()));
    }

    public int getRuleIndex(){
        return this.ruleIndex;
    }

    public God getGod(){
        return this.god;
    }
}



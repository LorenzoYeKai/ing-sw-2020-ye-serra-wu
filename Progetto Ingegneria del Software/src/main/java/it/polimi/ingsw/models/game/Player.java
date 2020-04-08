package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodType;

import java.util.Scanner;

public class Player implements PlayerData {

    private final String name;

    private Player nextPlayer;

    private boolean defeat = false;

    private final Worker[] workers = new Worker[2];

    private /*final*/ God god;

    public final Game game;

    public Player(Game game, String name) { //Nella creazione dei player saranno assegnati i rule index in modo crescente
        this.game = game;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /*private void setName(InputStream in, PrintStream out) { //Non servirà più visto che il nome si sceglie quando si entra nella lobby dove i player non sono ancora costruiti
        out.println("Choose a name: ");
        Scanner scanner = new Scanner(in);
        this.name = scanner.nextLine();
    }*/

    /**
     * Sets the God that will be used by this player
     */
    public void chooseCard() {
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

    /**
     * Creates and places the workers of this player on the World
     */
    public void setWorkers() {

        for (int i = 0; i < 2; i = i + 1) {
            workers[i] = new Worker(this);
            System.out.println("Choose  Worker position");
            Scanner scanner = new Scanner(System.in);
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            while (this.game.getWorld().getSpaces(x, y).isOccupied() || !this.game.getWorld().isInWorld(x, y)) {
                System.out.println("error,Choose Worker position");
                x = scanner.nextInt();
                y = scanner.nextInt();
            }
            workers[i].setPosition(x, y);
        }
    }

    public Worker selectWorker(int k) {
        return workers[k];
    }

    public boolean isDefeated() {
        return (!this.game.getRules().canMove(selectWorker(0).getX(), selectWorker(1).getY()));
    }

    public God getGod(){
        return this.god;
    }
}



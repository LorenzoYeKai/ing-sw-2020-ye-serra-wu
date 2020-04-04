package it.polimi.ingsw;

import java.util.Scanner;

public class Athena extends God {

    public Athena(ActualRule rules) {
        super(rules);
    }

    @Override
    public void move(Worker worker) { //Movable spaces display not implemented yet
        this.getRules().setRuleSets(worker.getPlayer().getRuleIndex(), new DefaultRule(worker.getWorld())); //Setta regole normali
        int currentX = worker.getX();
        int currentY = worker.getY();
        World world = worker.getWorld();
        System.out.println("Where should your worker move?");
        Scanner coordinates = new Scanner(System.in);
        while (true) { //Move loop (input control)
            int x = coordinates.nextInt();
            int y = coordinates.nextInt();
            if (this.getRules().canMoveThere(currentX, currentY, x, y)) { //Check coordinates validity
                if(world.levelDifference(currentX, currentY, x, y) == -1){
                    activePassivePower(worker); //Setta regole di athena se si muove in alto
                }
                System.out.println("Your worker moved form " + "[" + worker.getX() + "][" + worker.getY() + "] to " + "[" + x + "][" + y + "].");
                victory(x, y, worker); //Check win condition
                worker.setPosition(x, y);
                break;
            }
            System.out.println("You cannot move there!");
        }
    }

    /**
     * Metodo per settare regole
     */
    private void activePassivePower(Worker worker){
        this.getRules().setRuleSets(worker.getPlayer().getRuleIndex(), new AthenaRule(worker.getWorld()));
    }
}

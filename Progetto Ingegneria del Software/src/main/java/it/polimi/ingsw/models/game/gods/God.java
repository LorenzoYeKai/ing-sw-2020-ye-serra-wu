package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.Worker;

import java.util.Scanner;
import java.util.function.BiConsumer;

/**
 * Manages the default turn of a worker
 */
public abstract class God {

    /**
     * Default order of action performed by a worker in one turn
     */
    public void performActions(Worker worker){ //potrei usarlo solo e soltanto per ordinare le operazioni e aggiungerle a seconda del god
        perform(worker::move, "move");
        perform(worker::build, "build");

    }

    /**
     * Wrapper
     * @param consumer : takes a lambda with 2 integers
     * @param message : Displays the correct message
     */
    void perform(BiConsumer<Integer, Integer> consumer, String message){ //potrei metterlo in worker
        System.out.println("Where should your worker " + message + " ?");
        Scanner moveCoordinates = new Scanner(System.in);
        int x = moveCoordinates.nextInt();
        int y = moveCoordinates.nextInt();
        while(true){
            try{
                consumer.accept(x, y);
                break;
            }
            catch (IllegalArgumentException e){
                System.out.println("You cannot "+ message + " there!");
                System.out.println("Choose another space.");
                x = moveCoordinates.nextInt();
                y = moveCoordinates.nextInt();
            }
        }
    }

    /*public void deactivatePassivePower(Worker worker){
        worker.getRules().setRuleSets(worker.getPlayer().getRuleIndex(), new DefaultRule(worker.getWorld()));
    }*/





}

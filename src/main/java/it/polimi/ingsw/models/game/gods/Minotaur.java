package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.models.game.rules.DefaultRule;

/**
 * Not implemented yet
 */
public class Minotaur extends God {

    //Default action order

    @Override
    public void activateGodPower(ActualRule rules) {
        rules.addMovementRules("minotaurPower", (worker, target) -> {
            if(target.isOccupiedByWorker()){
                Vector2 destination = worker.getCurrentSpace().getPosition().getAfter(target.getPosition());
                return !worker.getIdentity().getPlayer().equals(target.getWorkerData().getPlayer()) &&
                        World.isInWorld(destination) &&
                        !worker.getWorld().get(destination).isOccupied();
            }
            return true;
        });
        rules.removeMovementRules("defaultIsFreeFromWorker");
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.removeMovementRules("minotaurPower");
        rules.addMovementRules("defaultIsFreeFromWorker", DefaultRule::defaultIsFreeFromWorker);
    }

    @Override
    public void forcePower(Worker worker, Space target){
        if(worker.getIdentity().getPlayer().equals(target.getWorkerData().getPlayer())){
            throw new InternalError("Cannot push your own worker");
        }
        worker.push(target.getWorkerData());
    }
}

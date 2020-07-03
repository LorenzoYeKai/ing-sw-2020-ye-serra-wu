package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.models.game.rules.DefaultRule;

import java.util.Objects;

public class Apollo extends God {

    //Default action order

    @Override
    public void forcePower(Worker worker, Space target){
        if(worker.getIdentity().getPlayer().equals(target.getWorkerData().getPlayer())){
            throw new InternalError("Cannot apply force power to self");
        }
        worker.swap(target.getWorkerData());
    }

    @Override
    public void activateGodPower(ActualRule rules) {
        // TODO: Check again
        rules.addMovementRules("apolloPower", (worker, target) -> {
            if(!target.isOccupiedByWorker()) {
                return true;
            }
            // if current and target are different player
            // then we can move by forcing the swap position
            return !Objects.equals(worker.getIdentity().getPlayer(), target.getWorkerData().getPlayer());
        });
        rules.removeMovementRules("defaultIsFreeFromWorker");
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.removeMovementRules("apolloPower");
        rules.addMovementRules("defaultIsFreeFromWorker", DefaultRule::defaultIsFreeFromWorker);

    }

}

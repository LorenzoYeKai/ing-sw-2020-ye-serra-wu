package it.polimi.ingsw.models.game.gods;


import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.WorldData;
import it.polimi.ingsw.models.game.rules.ActualRule;

import java.util.ArrayList;
import java.util.List;


public class Athena extends God {

    @Override
    public void onTurnStarted(ActualRule rules) {
        if (rules.getMovementRules().containsKey("athenaPower")) {
            // Athena herself shouldn't be limited by her power
            this.deactivateGodPower(rules);
        }
    }

    @Override
    public void onTurnEnded(Worker workerUsed, ActualRule rules) {
        WorldData initial = workerUsed.getWorld().getPrevious(2);
        WorldData afterMove = workerUsed.getWorld().getPrevious(1);
        //noinspection OptionalGetWithoutIsPresent
        Space initialSpace = initial.stream()
                .filter(space -> space.getWorkerData().equals(workerUsed.getIdentity()))
                .findFirst().get();
        //noinspection OptionalGetWithoutIsPresent
        Space spaceAfterMove = afterMove.stream()
                .filter(space -> space.getWorkerData().equals(workerUsed.getIdentity()))
                .findFirst().get();
        // check if Athena has moved up
        if (spaceAfterMove.getLevel() > initialSpace.getLevel()) {
            // if yes, activate Athena's god power limiting other players
            this.activateGodPower(rules);
        }
    }

    @Override
    public void activateGodPower(ActualRule rules) {
        rules.addMovementRules("athenaPower", Athena::cannotMoveUpPower);
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.getMovementRules().remove("athenaPower");
    }

    /**
     * Adds the currentSpace.levelDifference(targetSpace) > -1 condition to the rules
     * Used by athena and prometheus
     * Tested
     */
    public static boolean cannotMoveUpPower(Worker worker, Space target){
        return worker.getCurrentSpace().levelDifference(target) > 0;
    }
}

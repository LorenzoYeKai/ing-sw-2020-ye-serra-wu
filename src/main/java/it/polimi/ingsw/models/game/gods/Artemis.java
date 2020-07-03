package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.models.game.rules.DefaultRule;

import java.util.ArrayList;
import java.util.List;

public class Artemis extends God {

    @Override
    public void activateGodPower(ActualRule rules) {
        rules.removeMovementRules("defaultMoveWillBeFirstAction");
        rules.addMovementRules("artemisPower", (worker, target) -> {
            if(DefaultRule.defaultMoveWillBeFirstAction(worker, target)) {
                // Artemis can move as first action
                return true;
            }
            // But Artemis can also move as second action
            if(worker.getWorld().getNumberOfSavedPreviousWorlds() == 1) {
                if(worker.isLastActionMove()) {
                    // if Artemis have moved once, she can still move
                    // But she cannot move back to the initial position

                    //noinspection OptionalGetWithoutIsPresent
                    Space previous = worker.getPreviousSpace().get();
                    return !target.getPosition().equals(previous.getPosition());
                }
            }
            return false;
        });
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.addMovementRules("defaultMoveWillBeFirstAction", DefaultRule::defaultMoveWillBeFirstAction);
        rules.removeMovementRules("artemisPower");
    }

}

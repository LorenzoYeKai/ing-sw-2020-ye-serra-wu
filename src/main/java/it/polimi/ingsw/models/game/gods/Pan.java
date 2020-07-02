package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.rules.ActualRule;

/**
 * Not implemented yet
 */
public class Pan extends God {

    //Default action order

    @Override
    public void activateGodPower(ActualRule rules) {
        rules.addWinConditions("panPower", (worker, target) -> {
            return worker.getCurrentSpace().levelDifference(target) > 1;
        });
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.getMovementRules().remove("panPower");
    }

}

package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.models.game.rules.DefaultRule;

public class Pan extends God {

    //Default action order

    @Override
    public void activateGodPower(ActualRule rules) {
        rules.removeWinConditions("defaultWinCondition");
        rules.addWinConditions("panPower", (worker, target) -> {
            if (DefaultRule.defaultWinCondition(worker, target)) {
                return true;
            }
            return worker.getCurrentSpace().levelDifference(target) > 1;
        });
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.removeWinConditions("panPower");
        rules.addWinConditions("defaultWinCondition", DefaultRule::defaultWinCondition);
    }

}

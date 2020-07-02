package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.models.game.rules.DefaultRule;

public class Atlas extends God {

    //Default action order

    @Override
    public void activateGodPower(ActualRule rules) {
        rules.getBuildDomeRules().remove("defaultCanBuildDomeLevel");
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.addBuildDomeRules("defaultCanBuildDomeLevel", DefaultRule::defaultCanBuildDomeLevel);
    }

}

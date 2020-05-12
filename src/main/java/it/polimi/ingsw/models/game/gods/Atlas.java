package it.polimi.ingsw.models.game.gods;


import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.DefaultRule;

import java.util.ArrayList;
import java.util.List;

public class Atlas extends God {

    //Default action order

    @Override
    public void activateGodPower(Worker worker) {
        worker.getRules().getBuildDomeRules().remove("defaultCanBuildDomeLevel");
    }

    @Override
    public void deactivateGodPower(Worker worker) {
        worker.getRules().addBuildDomeRules("defaultCanBuildDomeLevel", DefaultRule::defaultCanBuildDomeLevel);
    }

    @Override
    public void forcePower(Worker worker, Space targetSpace) {
        throw new UnsupportedOperationException("Should be a fatal error");
    }
}

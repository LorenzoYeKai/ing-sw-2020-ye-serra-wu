package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.models.game.rules.DefaultRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;


public class Demeter extends God {

    @Override
    public void activateGodPower(ActualRule rules) {
        rules.removeBuildRules("defaultBuildAfterMove");
        rules.removeBuildDomeRules("defaultBuildAfterMove");

        rules.addBuildRules("demeterPower", Demeter::demeterPower);
        rules.addBuildDomeRules("demeterPower", Demeter::demeterPower);
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.removeBuildRules("demeterPower");
        rules.removeBuildDomeRules("demeterPower");
        rules.addBuildRules("defaultBuildAfterMove", DefaultRule::defaultBuildAfterMove);
        rules.addBuildDomeRules("defaultBuildAfterMove", DefaultRule::defaultBuildAfterMove);
    }

    private static boolean demeterPower(Worker worker, Space target) {
        // demeter can build after move
        if (DefaultRule.defaultBuildAfterMove(worker, target)) {
            return true;
        }
        // but demeter can also build after build, for one time
        if (worker.getWorld().getNumberOfSavedPreviousWorlds() == 2) {
            Optional<Space> previousBuild = worker.getPreviousBuild();
            if (previousBuild.isPresent()) {
                // but he cannot build twice at the same position
                return !previousBuild.get().getPosition()
                        .equals(target.getPosition());
            }
        }
        return false;
    }

}

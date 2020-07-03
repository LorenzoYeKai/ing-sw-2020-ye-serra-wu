package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.models.game.rules.DefaultRule;

import java.util.ArrayList;
import java.util.List;


public class Prometheus extends God {

    @Override
    public void activateGodPower(ActualRule rules) {
        // prometheus can build before move
        rules.removeBuildRules("defaultBuildAfterMove");
        rules.removeBuildDomeRules("defaultBuildAfterMove");
        // prometheus can move after build, not only as first action
        rules.removeMovementRules("defaultMoveWillBeFirstAction");

        rules.addBuildRules("prometheusPower", Prometheus::prometheusBuildPower);
        rules.addBuildDomeRules("prometheusPower", Prometheus::prometheusBuildPower);
        rules.addMovementRules("prometheusPower", Prometheus::prometheusMovePower);
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.addBuildRules("defaultBuildAfterMove", DefaultRule::defaultBuildAfterMove);
        rules.addBuildDomeRules("defaultBuildAfterMove", DefaultRule::defaultBuildAfterMove);
        rules.addMovementRules("defaultMoveWillBeFirstAction", DefaultRule::defaultMoveWillBeFirstAction);

        rules.removeBuildRules("prometheusPower");
        rules.removeBuildDomeRules("prometheusPower");
        rules.removeMovementRules("prometheusPower");
    }

    private static boolean prometheusBuildPower(Worker worker, Space target) {
        // prometheus can build:
        // 1. as first action
        // 2. as second action after a first move
        // 3. as third action after build - non-move-up
        switch (worker.getWorld().getNumberOfSavedPreviousWorlds()) {
            case 0 -> {
                return true;
            }
            case 1, 2 -> {
                // TODO: Add testing for move - move - build
                // TODO: Add testing for build - move up - build
                // prometheus cannot move twice
                // so if last action is move, it means prometheus either
                // has moved, or he has built, then moved.
                // And if prometheus has built before move, then he couldn't
                // move up.
                // So we don't even need to check if he had moved up, we just
                // need check if it has moved.
                if (worker.isLastActionMove()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean prometheusMovePower(Worker worker, Space target) {
        // prometheus can move:
        // 1. as first action
        // 2. as second action (first action must be build, and cannot move up)
        if (DefaultRule.defaultMoveWillBeFirstAction(worker, target)) {
            return true;
        }

        if (worker.getWorld().getNumberOfSavedPreviousWorlds() == 1) {
            // ensure prometheus had built
            if (worker.getPreviousBuild().isPresent()) {
                // he can move down or move parallel
                if (target.getLevel() <= worker.getCurrentSpace().getLevel()) {
                    return true;
                }
            }
        }

        return false;
    }


}

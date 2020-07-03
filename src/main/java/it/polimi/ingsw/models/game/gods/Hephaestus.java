package it.polimi.ingsw.models.game.gods;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.rules.ActualRule;
import it.polimi.ingsw.models.game.rules.DefaultRule;

import java.util.Optional;

/**
 * Not implemented yet
 */
public class Hephaestus extends God {

    @Override
    public void activateGodPower(ActualRule rules) {
        rules.removeBuildRules("defaultBuildAfterMove");
        rules.addBuildRules("hephaestusPower", (worker, target) -> {
            // hephaestus can build after move
            if(DefaultRule.defaultBuildAfterMove(worker, target)) {
                return true;
            }
            // but he can also build after build, for one time
            if (worker.getWorld().getNumberOfSavedPreviousWorlds() == 2) {
                Optional<Space> previousBuild = worker.getPreviouslyBuiltBlock();
                if(previousBuild.isPresent()) {
                    // he can build after build, on the same position
                    return previousBuild.get().getPosition()
                            .equals(target.getPosition());
                }
            }

            return false;
        });
    }

    @Override
    public void deactivateGodPower(ActualRule rules) {
        rules.removeBuildRules("hephaestusPower");
        rules.addBuildRules("defaultBuildAfterMove", DefaultRule::defaultBuildAfterMove);
    }

}

package it.polimi.ingsw.models.game.gods;


import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.rules.GodPower;


public class Athena extends God {

    /**
     * At the beginning of the turn deactivates the power
     * If the worker moves up activates the power
     * Needs to be changes after view implementation
     */
    @Override
    public void workerActionOrder(Worker worker){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /*private void activatePassivePower(Worker worker){
        worker.getRules().setRuleSets(worker.getPlayer().getRuleIndex(), new AthenaRule(worker.getWorld()));
    }*/

    /**
     * Adds Athena's power to the active rules
     */
    @Override
    public void activateGodPower(Worker worker) {
        worker.getRules().addMovementRules("athenaPower", GodPower::athenaPower);
    }
    /**
     * Removes Athena's power from the active rules
     */
    @Override
    public void deactivateGodPower(Worker worker){
        worker.getRules().getMovementRules().remove("athenaPower");
    }

    @Override
    public void forcePower(Worker worker, Space targetSpace) {
        throw new UnsupportedOperationException("Should be a fatal error");
    }

}

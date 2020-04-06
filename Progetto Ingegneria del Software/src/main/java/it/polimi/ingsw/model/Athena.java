package it.polimi.ingsw.model;


public class Athena extends God {

    /**
     * At the beginning of the turn deactivates the power
     * If the worker moves up activates the power
     */
    @Override
    public void performActions(Worker worker){
        deactivatePassivePower(worker);
        int originalX = worker.getX();
        int originalY = worker.getY();
        perform(worker::move, "move");
        if(worker.getWorld().levelDifference(originalX, originalY, worker.getX(), worker.getY()) == -1){
            activatePassivePower(worker);
        }
        perform(worker::build, "build");
    }

    /*private void activatePassivePower(Worker worker){
        worker.getRules().setRuleSets(worker.getPlayer().getRuleIndex(), new AthenaRule(worker.getWorld()));
    }*/

    /**
     * Adds Athena's power to the active rules
     */
    private void activatePassivePower(Worker worker){
        worker.getRules().getRuleSets().add(new AthenaRule(worker.getWorld()));
    }

    /**
     * Removes Athena's power from the active rules
     */
    private void deactivatePassivePower(Worker worker){
        worker.getRules().getRuleSets().stream()
        .filter(rule -> rule instanceof AthenaRule)
        .forEach(rule -> worker.getRules().getRuleSets().remove(rule));
    }
}

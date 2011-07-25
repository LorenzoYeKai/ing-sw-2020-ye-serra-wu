package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * Used by Worker
 * Merges all the active rules
 */
public class ActualRule implements Serializable {

    private final Map<String, BiPredicate<Worker, Space>> movementRules;
    private final Map<String, BiPredicate<Worker, Space>> buildRules;
    private final Map<String, BiPredicate<Worker, Space>> buildDomeRules;
    private final Map<String, BiPredicate<Worker, Space>> winConditions;
    private int domeLevel;
    private GodPower godPower;

    /**
     * The constructor creates the default rule
     */
    public ActualRule(World world) {
        this.movementRules = new HashMap<>();
        this.buildRules = new HashMap<>();
        this.buildDomeRules = new HashMap<>();
        this.winConditions = new HashMap<>();
        this.domeLevel = 3;
        this.godPower = new GodPower(world);
        resetDefaultRules();
    }

    /**
     * Checks all move related rules to determine if a dome can be built
     * at target space.
     * Used with {@link Worker#move(Space)}.
     *
     * @param worker the current {@link Space} of the worker.
     * @param target the destination target {@link Space}.
     * @return if is allowed to move in the target space.
     */
    public boolean canMoveThere(Worker worker, Space target) {
        return this.movementRules.values().stream()
                .allMatch(predicate -> predicate.test(worker, target));
    }

    /**
     * Checks all build related rules to determine if a block can be built
     * at target space.
     * Used with {@link Worker#buildBlock(Space)}}.
     *
     * @param worker the current {@link Space} of the worker.
     * @param target the target {@link Space} to build a block.
     * @return if is allowed to build a dome in the target space.
     */
    public boolean canBuildThere(Worker worker, Space target) {
        return this.buildRules.values().stream()
                .allMatch(predicate -> predicate.test(worker, target));
    }

    /**
     * Checks all build-dome related rules to determine if a dome can be built
     * at target space.
     * Used with {@link Worker#buildDome(Space)}.
     *
     * @param worker the current {@link Space} of the worker.
     * @param target the target {@link Space} to build a dome.
     * @return if is allowed to build a dome in the target space.
     */
    public boolean canBuildDomeThere(Worker worker, Space target) {
        return this.buildDomeRules.values().stream()
                .allMatch(predicate -> predicate.test(worker, target));
    }

    /**
     * Merges all the winCondition methods of all the active rules
     * Used in {@link Worker#victory(Space)}
     * @param worker the current {@link Space} of the worker.
     * @param target the target {@link Space}
     * @return if it will win
     */
    public boolean winCondition(Worker worker, Space target) {
        return this.winConditions.values().stream()
                .anyMatch(predicate -> predicate.test(worker, target));
    }

    public List<WorkerActionType> possibleActions(int phase, Worker worker){
        return worker.getPlayer().getGod().workerActionOrder(phase, worker);
    }

    private void resetDefaultRules() {
        this.movementRules.clear();
        this.buildRules.clear();
        this.buildDomeRules.clear();
        this.winConditions.clear();

        //movement

        this.movementRules.put("defaultIsNeighbor", DefaultRule::defaultIsNeighbor);
        this.movementRules.put("defaultLevelDifference", DefaultRule::defaultLevelDifference);
        this.movementRules.put("defaultIsFreeFromWorker", DefaultRule::defaultIsFreeFromWorker);
        this.movementRules.put("defaultIsFreeByDome", DefaultRule::defaultIsFreeFromDome);
        this.movementRules.put("defaultIsInWorld", DefaultRule::defaultIsInWorld);

        //build

        this.buildRules.put("defaultIsNeighbor", DefaultRule::defaultIsNeighbor);
        this.buildRules.put("defaultIsInWorld", DefaultRule::defaultIsInWorld);
        this.buildRules.put("defaultIsFree", DefaultRule::defaultIsFree);
        this.buildRules.put("defaultBuildLevelLimit", DefaultRule::defaultBuildLevelLimit);

        //win condition

        this.winConditions.put("defaultWinCondition", DefaultRule::defaultWinCondition);

        //build dome

        this.buildDomeRules.put("defaultIsNeighbor", DefaultRule::defaultIsNeighbor);
        this.buildDomeRules.put("defaultIsInWorld", DefaultRule::defaultIsInWorld);
        this.buildDomeRules.put("defaultIsFree", DefaultRule::defaultIsFree);
        this.buildDomeRules.put("defaultCanBuildDomeLevel", DefaultRule::defaultCanBuildDomeLevel);
    }

    public Map<String, BiPredicate<Worker, Space>> getMovementRules() {
        return this.movementRules;
    }

    public Map<String, BiPredicate<Worker, Space>> getBuildRules() {
        return this.buildRules;
    }

    public Map<String, BiPredicate<Worker, Space>> getWinConditions() {
        return this.winConditions;
    }

    public Map<String, BiPredicate<Worker, Space>> getBuildDomeRules() {
        return this.buildDomeRules;
    }


    public void addMovementRules(String key, BiPredicate<Worker, Space> value) {
        this.movementRules.put(key, value);
    }

    public void addBuildRules(String key, BiPredicate<Worker, Space> value) {
        this.buildRules.put(key, value);
    }

    public void addWinConditions(String key, BiPredicate<Worker, Space> value) {
        this.winConditions.put(key, value);
    }

    public void addBuildDomeRules(String key, BiPredicate<Worker, Space> value) {
        this.buildDomeRules.put(key, value);
    }

    @Deprecated
    public void setDomeLevel(int level) {
        this.domeLevel = level;
    }

}

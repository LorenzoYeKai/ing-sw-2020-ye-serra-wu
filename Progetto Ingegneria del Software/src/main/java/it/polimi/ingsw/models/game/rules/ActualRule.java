package it.polimi.ingsw.models.game.rules;

import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * Used by Worker
 * Merges all the active rules
 */
public class ActualRule {

    private final Map<String, BiPredicate<Space, Space>> movementRules;
    private final Map<String, BiPredicate<Space, Space>> buildRules;
    private final Map<String, BiPredicate<Space, Space>> buildDomeRules;
    private final Map<String, BiPredicate<Space, Space>> winConditions;
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
     * Used with {@link it.polimi.ingsw.models.game.Worker#move(Space)}.
     *
     * @param currentSpace the current {@link Space} of the worker.
     * @param targetSpace the destination target {@link Space}.
     * @return if is allowed to move in the target space.
     */
    public boolean canMoveThere(Space currentSpace, Space targetSpace) {
        return this.movementRules.values().stream()
                .allMatch(predicate -> predicate.test(currentSpace, targetSpace));
    }

    /**
     * Checks all build related rules to determine if a block can be built
     * at target space.
     * Used with {@link it.polimi.ingsw.models.game.Worker#buildBlock(Space)}}.
     *
     * @param currentSpace the current {@link Space} of the worker.
     * @param targetSpace the target {@link Space} to build a block.
     * @return if is allowed to build a dome in the target space.
     */
    public boolean canBuildThere(Space currentSpace, Space targetSpace) {
        return this.buildRules.values().stream()
                .allMatch(predicate -> predicate.test(currentSpace, targetSpace));
    }

    /**
     * Checks all build-dome related rules to determine if a dome can be built
     * at target space.
     * Used with {@link it.polimi.ingsw.models.game.Worker#buildDome(Space)}.
     *
     * @param currentSpace the current {@link Space} of the worker.
     * @param targetSpace the target {@link Space} to build a dome.
     * @return if is allowed to build a dome in the target space.
     */
    public boolean canBuildDomeThere(Space currentSpace, Space targetSpace) {
        return this.buildDomeRules.values().stream()
                .allMatch(predicate -> predicate.test(currentSpace, targetSpace));
    }

    /**
     * Merges all the winCondition methods of all the active rules
     * Used in Worker.victory
     */
    public boolean winCondition(Space currentSpace, Space targetSpace) {
        return this.winConditions.values().stream()
                .anyMatch(predicate -> predicate.test(currentSpace, targetSpace));
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

    public Map<String, BiPredicate<Space, Space>> getMovementRules() {
        return this.movementRules;
    }

    public Map<String, BiPredicate<Space, Space>> getBuildRules() {
        return this.buildRules;
    }

    public Map<String, BiPredicate<Space, Space>> getWinConditions() {
        return this.winConditions;
    }

    public GodPower getGodPower() {
        return this.godPower;
    }

    public int getDomeLevel() {
        return this.domeLevel;
    }

    public void addMovementRules(String key, BiPredicate<Space, Space> value) {
        this.movementRules.put(key, value);
    }

    public void addBuildRules(String key, BiPredicate<Space, Space> value) {
        this.buildRules.put(key, value);
    }

    public void addWinConditions(String key, BiPredicate<Space, Space> value) {
        this.winConditions.put(key, value);
    }

    public void addBuildDomeRules(String key, BiPredicate<Space, Space> value) {
        this.buildDomeRules.put(key, value);
    }

    public void setDomeLevel(int level) {
        this.domeLevel = level;
    }

}

package it.polimi.ingsw.models.game;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.rules.ActualRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class represents the pawn
 * It contain all information relating to the player, world and rules.
 */
public class Worker {
    private final WorkerData identity;
    private final Player player;
    private final World world;
    private final ActualRule rules;
    private Space currentSpace;

    public Worker(Player player, int index) {
        this.identity = new WorkerData(player, index);
        this.player = player;
        this.world = this.player.getGame().getWorld();
        this.rules = this.player.getGame().getRules();
        this.currentSpace = null;
    }

    /**
     * Get the {@link WorkerData} that identifies this worker.
     *
     * @return the {@link WorkerData} that identifies this worker
     */
    public WorkerData getIdentity() {
        return this.identity;
    }

    public Space getCurrentSpace() {
        return this.currentSpace;
    }

    public Player getPlayer() {
        return this.player;
    }

    public World getWorld() {
        return this.world;
    }

    public ActualRule getRules() {
        return this.rules;
    }

    /**
     * Computes the available spaces for movement.
     *
     * @return the list of spaces available for movement.
     */
    public List<Space> computeAvailableSpaces() {
        return this.world.getData().stream()
                .filter(space -> rules.canMoveThere(this, space))
                .collect(Collectors.toList());
    }

    /**
     * Computes the spaces where the worker is allowed to build a block
     *
     * @return the list of spaces available for building blocks.
     */
    public List<Space> computeBuildableSpaces() {
        return this.world.getData().stream()
                .filter(space -> rules.canBuildThere(this, space))
                .collect(Collectors.toList());
    }

    /**
     * Computes the spaces where the worker is allowed to build a dome
     */
    public List<Space> computeDomeSpaces() {
        return this.world.getData().stream()
                .filter(space -> rules.canBuildDomeThere(this, space))
                .collect(Collectors.toList());
    }

    /**
     * Computes the spaces where the worker will win by moving there
     */
    public List<Space> computeWinSpaces() {
        return this.world.getData().stream()
                .filter(space -> rules.winCondition(this, space))
                .collect(Collectors.toList());
    }

    /**
     * Get the possible actions in the current phase
     *
     * @return the possible actions for the current phase
     */
    public final Map<WorkerActionType, List<Vector2>> computePossibleActions() {
        Function<List<Space>, List<Vector2>> spaceToPosition = list ->
                list.stream().map(Space::getPosition).collect(Collectors.toList());
        Map<WorkerActionType, List<Vector2>> result = new HashMap<>();
        List<Vector2> possibleMoves = spaceToPosition.apply(this.computeAvailableSpaces());
        List<Vector2> possibleBuilds = spaceToPosition.apply(this.computeBuildableSpaces());
        List<Vector2> possibleDomes = spaceToPosition.apply(this.computeDomeSpaces());
        List<Vector2> possibleWins = spaceToPosition.apply(this.computeWinSpaces());

        result.put(WorkerActionType.MOVE, possibleMoves);
        result.put(WorkerActionType.BUILD, possibleBuilds);
        result.put(WorkerActionType.BUILD_DOME, possibleDomes);
        result.put(WorkerActionType.WIN, possibleWins);
        return result;
    }

    /**
     * Get the previous space occupied by the worker in the current turn.
     *
     * @return the previous space occupied by the worker in the current turn,
     * or {@link Optional#empty()} if this worker hasn't performed any action
     * yet in this turn.
     */
    public Optional<Space> getPreviousSpace() {
        return this.world.peekPrevious().flatMap(previousWorld ->
                previousWorld.stream().filter(space ->
                        this.identity.equals(space.getWorkerData())
                ).findAny()
        );
    }

    /**
     * Check whether the last action of this worker in the current turn is MOVE.
     *
     * @return true if the last action of this worker in the current turn is
     * movement.
     */
    public boolean isLastActionMove() {
        // forced move will not be (erroneously) detected as movement, because
        // workers cannot be force-moved in their own turn, and after each turn,
        // World.getPrevious() will be reset.

        // get the position of previousSpace, and check whether
        // this.currentSpace.getPosition() equals to the previous position.
        // if the position has changed, then we can assume the last action is
        // move.
        Space previous = this.getPreviousSpace()
                .orElseThrow(() -> new InternalError("Should not be called because there aren't any previous actions yet"));
        return !this.currentSpace.getPosition().equals(previous.getPosition());
    }

    /**
     * Get the {@link Space} in the current {@link World} where the worker
     * has built a block in the previous action.
     *
     * @return the {@link Space} on which a block has been built previously,
     * or {@link Optional#empty()} if worker didn't build in the last action,
     * or if this worker hasn't performed any action yet in the current turn.
     */
    public Optional<Space> getPreviouslyBuiltBlock() {
        return this.world.peekPrevious().flatMap(previousWorld ->
                previousWorld.stream().filter(previousSpace -> {
                    Space current = this.world.get(previousSpace.getPosition());
                    return previousSpace.getLevel() < current.getLevel();
                }).findAny()
        );
    }

    /**
     * Get the {@link Space} in the current {@link World} where the worker
     * has built a dome in the previous action.
     *
     * @return the {@link Space} on which a dome has been built previously,
     * or {@link Optional#empty()} if worker didn't build dome in the last
     * action, or if this worker hasn't performed any action yet in the current
     * turn.
     */
    public Optional<Space> getPreviouslyBuiltDome() {
        return this.world.peekPrevious().flatMap(previousWorld ->
                previousWorld.stream().filter(previousSpace -> {
                    Space current = this.world.get(previousSpace.getPosition());
                    return !previousSpace.isOccupiedByDome() && current.isOccupiedByDome();
                }).findAny()
        );
    }

    /**
     * Get the {@link Space} in the current {@link World} where the worker
     * has built a block or a dome in the previous action.
     *
     * @return the {@link Space} on which a block or a dome has been built
     * previously, or {@link Optional#empty()} if worker didn't build in the
     * last action, or if this worker hasn't performed any action yet in the
     * current turn.
     */
    public Optional<Space> getPreviousBuild() {
        return this.getPreviouslyBuiltBlock().or(this::getPreviouslyBuiltDome);
    }

    /**
     * Set the start position
     *
     * @param startPosition the space where worker should stay.
     */
    public void setStartPosition(Space startPosition) {
        if (this.currentSpace != null) {
            throw new InternalError("Should not set start position");
        }
        this.currentSpace = startPosition.setWorker(this.identity);
        this.world.update(this.currentSpace);
    }

    /**
     * Move a worker.
     * The move conditions should be already checked in the controller.
     *
     * @param targetSpace the destination space
     */
    public void move(Space targetSpace) {
        this.victory(targetSpace); //Check win condition
        this.setAndUpdatePosition(targetSpace);
    }

    /**
     * Used by worker to build.
     * The build conditions should be already checked in the controller.
     *
     * @param targetSpace the destination space
     */
    public void buildBlock(Space targetSpace) {
        this.world.update(targetSpace.addLevel());
    }

    /**
     * Used by worker to build a dome
     * The build conditions should be already checked in the controller.
     *
     * @param targetSpace the destination space
     */
    public void buildDome(Space targetSpace) {
        this.world.update(targetSpace.setDome());
    }

    /**
     * Checks if a movement is satisfying a win condition
     */
    public void victory(Space targetSpace) { //This method is called only after checking that the worker can move to that position
        if (!this.world.isNotificationEnabled()) {
            return;
        }
        if (this.rules.winCondition(this, targetSpace)) {
            this.setAndUpdatePosition(targetSpace);
            this.player.getGame().announceVictory(this.player); //If true the game ends
        }
    }

    /**
     * Swap the position between two workers.
     * Used by {@link it.polimi.ingsw.models.game.gods.Apollo}'s God Power.
     *
     * @param target the target worker.
     */
    public void swap(WorkerData target) {
        Worker targetWorker = this.player.getGame().getWorker(target);
        if (this.equals(targetWorker)) {
            throw new InternalError("Cannot swap self");
        }
        try (var batchUpdateController = this.world.beginBatchUpdate()) {
            Space targetSpace = targetWorker.currentSpace;
            targetWorker.currentSpace = this.currentSpace.setWorker(target);
            this.move(targetSpace);
            // targetWorker.currentSpace is the previous space of this
            // so it needs to be updated after this.move
            this.world.update(targetWorker.currentSpace);
        }
    }

    /**
     * Push another worker.
     * Used by {@link it.polimi.ingsw.models.game.gods.Minotaur}'s God Power.
     *
     * @param target the target worker.
     */
    public void push(WorkerData target) {
        Worker targetWorker = this.player.getGame().getWorker(target);
        if (this.equals(targetWorker)) {
            throw new InternalError("Cannot push self");
        }
        Vector2 ourPosition = this.currentSpace.getPosition();
        Vector2 targetPosition = targetWorker.currentSpace.getPosition();
        Vector2 destination = ourPosition.getAfter(targetPosition);
        if (!World.isInWorld(destination)) {
            throw new InternalError("Cannot push");
        }
        try (var batchUpdateController = this.world.beginBatchUpdate()) {
            Space targetSpace = targetWorker.currentSpace;
            targetWorker.currentSpace = this.world.get(destination).setWorker(target);
            this.world.update(targetWorker.currentSpace);
            this.move(targetSpace);
        }
    }

    /**
     * Reset the worker to a specified space that already contains a matching
     * {@link WorkerData}. Used mainly for undo.
     *
     * @param previousSpace the target space.
     */
    public void reset(Space previousSpace) {
        if (!this.identity.equals(previousSpace.getWorkerData())) {
            throw new InternalError("Invalid undo");
        }
        this.currentSpace = previousSpace;
    }

    //

    /**
     * Remove the worker when the player loses
     */
    public void removeWorkerWhenDefeated() {
        this.world.update(this.currentSpace.setWorker(null));
        this.currentSpace = null;
    }

    private void setAndUpdatePosition(Space target) {
        Space previousSpace = this.currentSpace.setWorker(null);
        this.currentSpace = target.setWorker(this.identity);
        this.world.update(previousSpace, this.currentSpace);
    }
}


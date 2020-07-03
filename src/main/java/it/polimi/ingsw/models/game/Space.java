package it.polimi.ingsw.models.game;

import it.polimi.ingsw.InternalError;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 *
 * This class represents a box on the board so inside we keep all of
 * information on the levels of the boxes and their occupation
 */
public class Space implements Serializable {
    private final Vector2 position;
    private final int level;
    private final WorkerData worker;
    private final boolean occupiedByDome;

    public Space(int x, int y) {
        this(new Vector2(x, y), 0, null, false);
    }

    private Space(Vector2 position, int level, WorkerData worker, boolean occupiedByDome) {
        this.position = position;
        this.level = level;
        this.worker = worker;
        this.occupiedByDome = occupiedByDome;
    }

    public Vector2 getPosition() {
        return this.position;
    }

    /**
     * Calculates the level difference between 2 spaced
     * Returns negative if moving up
     * Returns positive if moving down
     * Returns 0 if moving in same level
     * TODO: CHECK AGAIN
     */
    public int levelDifference(Space other) {
        return this.level - other.getLevel();
    }

    public int getLevel() {
        return this.level;
    }

    /**
     *
     *
     * @return return the workerdata of the worker who occupies it returns
     */
    public WorkerData getWorkerData() {
        return this.worker;
    }

    /**
     *
     *
     * @return true if it is occupied by a Worker
     */
    public boolean isOccupiedByWorker() { return this.worker != null; }

    /**
     *
     *
     * @return true if it is occupied by a Dome
     */
    public boolean isOccupiedByDome() {
        return this.occupiedByDome;
    }

    /**
     *
     * @return true if it is occupied
     */
    public boolean isOccupied() {
        return this.isOccupiedByWorker() || this.isOccupiedByDome();
    }

    /**
     * Create a new {@link Space} that is same with current
     * {@link Space} but with one more level.
     * @return the new {@link Space} with one more level.
     */
    public Space addLevel() {
        // TODO: CONVERT LITERAL 3 TO CLASS CONSTANT
        if(this.level > 3) {
            throw new InternalError("You cannot build any further!");
        }
        if(this.isOccupied()) {
            throw new InternalError("Cannot build on occupied space");
        }
        return new Space(this.position, this.level + 1, this.worker, this.occupiedByDome);
    }

    /**
     * Create a new {@link Space} that is same with current
     * {@link Space} but which is being occupied by dome.
     * @return the new {@link Space} occupied by dome.
     */
    public Space setDome() {
        if(this.isOccupied()) {
            throw new InternalError("Cannot build on occupied space");
        }
        return new Space(this.position, this.level, this.worker, true);
    }

    /**
     * Create a new {@link Space} that is same with current
     * {@link Space} but with a different optional worker.
     * @param worker the new worker (may be null).
     * @return the new {@link Space} with the new worker or without worker.
     */
    public Space setWorker(WorkerData worker) {
        if(this.isOccupiedByDome()) {
            throw new InternalError("Cannot move to dome space");
        }
        return new Space(this.position, this.level, worker, this.occupiedByDome);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Space space = (Space) o;
        return level == space.level &&
                occupiedByDome == space.occupiedByDome &&
                position.equals(space.position) &&
                Objects.equals(worker, space.worker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, level, worker, occupiedByDome);
    }
}

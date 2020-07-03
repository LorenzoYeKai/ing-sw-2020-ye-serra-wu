package it.polimi.ingsw.models.game;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.Notifiable;

import java.util.*;

public class World {
    /**
     * @see World#beginBatchUpdate()
     */
    public interface BatchUpdateController extends AutoCloseable {
        @Override
        void close();
    }

    public static final int SIZE = 5;
    private final Notifiable<Space> onSpaceChanged;
    private boolean enableNotification = true;
    private List<Space> pendingChanges; // see beginBatchUpdate
    private WorldData data; // current world
    private final LinkedList<WorldData> previousWorlds;

    public static boolean isInWorld(int x, int y) {
        return y >= 0 && y < World.SIZE && x >= 0 && x < World.SIZE;
    }

    public static boolean isInWorld(Vector2 coordinates) {
        return World.isInWorld(coordinates.getX(), coordinates.getY());
    }

    /**
     * Creates an empty world
     */
    public World(Notifiable<Space> onSpaceChanged) {
        this.onSpaceChanged = onSpaceChanged;
        this.pendingChanges = null;
        this.previousWorlds = new LinkedList<>();
        List<Space> spaces = new ArrayList<>(World.SIZE * World.SIZE);
        for (int y = 0; y < World.SIZE; ++y) {
            for (int x = 0; x < World.SIZE; ++x) {
                spaces.add(new Space(x, y));
            }
        }
        this.data = new WorldData(spaces);
    }

    public Space get(int x, int y) {
        return this.getData().get(x, y);
    }

    public Space get(Vector2 coordinates) {
        return this.getData().get(coordinates);
    }

    /**
     * Disable notification. When notification is disabled, any changes to the
     * world will not trigger {@link #onSpaceChanged}. Notification is on by
     * default.
     * Used mainly by {@link WorkerActionPredictor}.
     */
    public void disableNotifications() {
        this.enableNotification = false;
    }

    /**
     * Enable notification. Notification is enabled by default.
     */
    public void enableNotifications() {
        this.enableNotification = true;
    }

    /**
     * Starts the batch update mode. After calling this function,
     * subsequent calls to {@link this#update(Space...)} will not trigger
     * notifications or save World as "previous world", instead all changes
     * will be cached until the returned object's
     * {@link BatchUpdateController#close()} has been called, then everything
     * will be flushed.
     * <p>
     * Used mainly by {@link Worker#push(WorkerData)} and
     * {@link Worker#swap(WorkerData)}.
     *
     * @return an {@link BatchUpdateController} which will flush changes when closed.
     */
    public BatchUpdateController beginBatchUpdate() {
        if(this.pendingChanges != null) {
            throw new InternalError("Already in batch update mode");
        }
        this.pendingChanges = new ArrayList<>();
        return () -> {
            this.update(this.pendingChanges);
            this.pendingChanges = null;
        };
    }

    /**
     * Update the {@link World} with new {@link Space}.
     * If multiple spaces with the same coordinates are passed to this
     * function, only the last of them will be used.
     * Will automatically save history and notify views, except when in batch
     * update mode (see {@link this#beginBatchUpdate()})
     *
     * @param newSpaces the new spaces to be updated.
     */
    public void update(Space... newSpaces) {
        List<Space> newSpaceList = Arrays.asList(newSpaces);
        if(this.pendingChanges != null) {
            this.pendingChanges.addAll(newSpaceList);
        }
        else {
            this.update(newSpaceList);
        }
    }

    public WorldData getData() {
        return this.data;
    }

    /**
     * Get a previous world by index.
     * 0 is the most recent previous world.
     * @return a previous world.
     */
    public WorldData getPrevious(int index) {
        if(index >= this.getNumberOfSavedPreviousWorlds()) {
            throw new InternalError("Out of range");
        }
        return this.previousWorlds.get(index);
    }

    /**
     * Get the previous world, if it exists.
     * @return The most recent previous world, or {@link Optional#empty()} if
     * it doesn't exist.
     */
    public Optional<WorldData> peekPrevious() {
        if(this.previousWorlds.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.previousWorlds.peekFirst());
    }

    /**
     * Reset the world to the previous world, if a previous world exists.
     * Note: {@link Worker} contains {@link Space} which aren't controlled
     * by the world, so they need to be updated separately.
     */
    public void revertWorld() {
        if(this.previousWorlds.isEmpty()) {
            throw new InternalError("Cannot revert world because history is empty");
        }
        this.data = this.previousWorlds.removeFirst();
        if(!this.enableNotification) {
            return;
        }
        for(Space space : this.data) {
            this.onSpaceChanged.notify(space);
        }
    }

    /**
     *
     *
     I use this to be able to calculate the phase of the turn


     * @return return the number of actions done this turn
     */
    public int getNumberOfSavedPreviousWorlds() {
        return this.previousWorlds.size();
    }

    /**
     *eliminate all instances of the world when the turn changes
     */
    public void clearPreviousWorlds() {
        this.previousWorlds.clear();
    }

    private void update(List<Space> newSpaces) {
        this.previousWorlds.addFirst(this.data);
        this.data = this.data.update(newSpaces);
        if(!this.enableNotification) {
            return;
        }
        for(Space space : newSpaces) {
            this.onSpaceChanged.notify(space);
        }
    }
}



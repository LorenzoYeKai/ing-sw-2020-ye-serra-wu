package it.polimi.ingsw.models.game;

import it.polimi.ingsw.controller.game.WorkerActionType;

public class WorkerActionPredictor {
    private final Game game;
    private boolean hasMoved = false;
    private boolean isValid = false;

    public WorkerActionPredictor(Game game, boolean hasAlreadyMoved) {
        this.game = game;
        this.hasMoved = hasAlreadyMoved;
    }

    /**
     * Verify if worker can get at least one valid action sequence (move then
     * build, or win), by performing an action.
     * If worker does not have any valid action sequence, then this worker is
     * not usable. If both worker of a player aren't usable, then player will
     * lose.
     *
     * @param worker   the worker to be checked.
     * @param type     the type of initial action, this must already be a valid
     *                 action to be performed on the worker
     * @param position the target position of initial action. This must
     *                 already be a valid target space for the specified
     *                 initial action.
     * @return true if this initial action
     */
    public boolean verify(Worker worker, WorkerActionType type, Vector2 position) {
        this.game.getWorld().disableNotifications();
        tryNextAction(worker, type, this.game.getWorld().get(position));
        this.game.getWorld().enableNotifications();
        return this.isValid;
    }

    private void findValidPath(Worker worker) {
        // compute the current possible actions
        var possibleActions = worker.computePossibleActions();
        // try each possible action
        for (WorkerActionType type : possibleActions.keySet()) {
            // try each possible action on every possible position
            for (Vector2 position : possibleActions.get(type)) {
                Space space = game.getWorld().get(position);
                tryNextAction(worker, type, space);
                // if this has already be verified as valid,
                // then we don't need to keep trying
                if (this.isValid) {
                    return;
                }
            }
        }
    }

    private void tryNextAction(Worker worker, WorkerActionType type, Space space) {
        boolean needUndo = false;
        switch (type) {
            case MOVE -> {
                needUndo = true;
                if (space.isOccupied()) {
                    worker.getPlayer().getGod().forcePower(worker, space);
                } else {
                    worker.move(space);
                }
                // worker has moved
                this.hasMoved = true;
                this.findValidPath(worker);
            }
            case BUILD, BUILD_DOME -> {
                // if worker has built after move, then this is a
                // valid action sequence
                if (hasMoved) {
                    this.isValid = true;
                } else {
                    needUndo = true;
                    // otherwise continue to try to find a valid path
                    if (type == WorkerActionType.BUILD) {
                        worker.buildBlock(space);
                    } else {
                        worker.buildDome(space);
                    }
                    this.findValidPath(worker);
                }
            }
            // if worker can win, then this is valid
            case WIN -> this.isValid = true;
        }
        // after trying, revert the world.
        if (needUndo) {
            game.gameUndo();
        }

    }
}

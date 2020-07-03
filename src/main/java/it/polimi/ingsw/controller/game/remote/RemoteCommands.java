package it.polimi.ingsw.controller.game.remote;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.Vector2;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.views.game.GameView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum RemoteCommandType {
    JOIN_GAME,

}

interface RemoteCommand extends Serializable {
    Serializable apply(GameController controller)
            throws NotExecutedException, IOException;
}

/**
 * A special command which corresponds to
 * {@link GameController#joinGame(String, GameView)}
 */
final class JoinGameCommand implements Serializable {
    private final String nickname;

    public JoinGameCommand(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}

/**
 * A command which corresponds to
 * {@link GameController#workerAction(String, WorkerActionType, int, int)}
 */
final class WorkerAction implements RemoteCommand {
    private final String player;
    private final WorkerActionType type;
    private final Vector2 position;

    public WorkerAction(String player, WorkerActionType type, Vector2 position) {
        this.player = player;
        this.type = type;
        this.position = position;
    }
    @Override
    public Serializable apply(GameController controller)
            throws NotExecutedException, IOException {
        controller.workerAction(player, type, position.getX(), position.getY());
        return null;
    }
}

/**
 * A command which can be used for
 * {@link GameController#addAvailableGods(GodType)} and
 * {@link GameController#removeAvailableGod(GodType)}
 */
final class GodCommand implements RemoteCommand {
    public enum Type {
        ADD,
        REMOVE
    }
    private final Type type;
    private final GodType god;

    public GodCommand(Type type, GodType god) {
        this.type = type;
        this.god = god;
    }

    @Override
    public Serializable apply(GameController controller)
            throws NotExecutedException, IOException {
        switch (type) {
            case ADD -> controller.addAvailableGods(god);
            case REMOVE -> controller.removeAvailableGod(god);
        }
        return null;
    }
}

/**
 * A command which is used for
 * {@link GameController#setPlayerGod(String, GodType)}
 */
final class ChooseGodCommand implements RemoteCommand {
    private final String player;
    private final GodType god;

    public ChooseGodCommand(String player, GodType god) {
        this.player = player;
        this.god = god;
    }
    @Override
    public Serializable apply(GameController controller)
            throws NotExecutedException, IOException {
        controller.setPlayerGod(player, god);
        return null;
    }
}

/**
 * A command which corresponds to
 * {@link GameController#setGameStatus(GameStatus)}
 */
final class GameStatusCommand implements RemoteCommand {
    private final GameStatus newStatus;

    public GameStatusCommand(GameStatus newStatus) {
        this.newStatus = newStatus;
    }

    @Override
    public Serializable apply(GameController controller)
            throws NotExecutedException, IOException {
        controller.setGameStatus(newStatus);
        return null;
    }
}

/**
 * Can be used to represent all
 * {@link GameController}'s commands without a parameter.
 * For example {@link GameController#nextTurn()},
 * {@link GameController#undo()}
 */
final class MiscellaneousCommand implements RemoteCommand {
    public enum Type {
        NEXT_TURN,
        RESET_TURN,
        UNDO
    }

    private final Type type;

    public MiscellaneousCommand(Type type) {
        this.type = type;
    }

    @Override
    public Serializable apply(GameController controller) throws NotExecutedException, IOException {
        switch (type) {
            case NEXT_TURN -> controller.nextTurn();
            case UNDO -> controller.undo();
        }
        return null;
    }
}

/**
 * A command which corresponds to {@link GameController#setCurrentPlayer(int)}
 */
final class SetCurrentPlayerCommand implements RemoteCommand {
    private final int index;

    public SetCurrentPlayerCommand(int index) {
        this.index = index;
    }

    @Override
    public Serializable apply(GameController controller) throws NotExecutedException, IOException {
        controller.setCurrentPlayer(index);
        return null;
    }
}

/**
 * A command which corresponds to {@link GameController#selectWorker(int)}
 */
final class SelectWorkerCommand implements RemoteCommand {
    private final int index;

    public SelectWorkerCommand(int index) {
        this.index = index;
    }

    @Override
    public Serializable apply(GameController controller)
            throws NotExecutedException, IOException {
        controller.selectWorker(index);
        return null;
    }
}

/**
 * A command which corresponds to {@link GameController#getValidActions()}
 */
final class GetValidActionsCommand implements RemoteCommand {
    @Override
    public Serializable apply(GameController controller)
            throws NotExecutedException, IOException {
        HashMap<WorkerActionType, ArrayList<Vector2>> result = new HashMap<>();
        Map<WorkerActionType, List<Vector2>> validActions = controller.getValidActions();
        for(WorkerActionType type : validActions.keySet()) {
            result.put(type, new ArrayList<>(validActions.get(type)));
        }
        return result;
    }
}
package it.polimi.ingsw.controller.game.remote;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.Vector2;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.requests.RequestProcessor;
import it.polimi.ingsw.views.game.GameView;
import it.polimi.ingsw.views.game.remote.ClientGameView;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ClientGameController implements GameController {
    private final RequestProcessor connection;
    private ClientGameView view;

    public ClientGameController(RequestProcessor connection) {
        this.connection = connection;
    }

    @Override
    public void joinGame(String nickname, GameView view)
            throws NotExecutedException, IOException {
        if (this.view != null) {
            throw new InternalError("ClientGameController already being used");
        }
        this.view = new ClientGameView(view);
        this.connection.addHandler(this.view);
        JoinGameCommand command = new JoinGameCommand(nickname);
        try {
            this.connection.remoteInvoke(command);
        } catch (Throwable e) {
            // TODO: Handle failures in a better way,
            //  so all methods (not just this one) can handle network errors
            // cleanup: remove handlers
            this.connection.removeHandler(this.view);
            this.view = null;
            throw e;
        }
    }

    @Override
    public void workerAction(String player,
                             WorkerActionType action,
                             int x, int y)
            throws NotExecutedException, IOException {
        WorkerAction workerAction =
                new WorkerAction(player, action, new Vector2(x, y));
        this.connection.remoteInvoke(workerAction);
    }

    @Override
    public void addAvailableGods(GodType type)
            throws NotExecutedException, IOException {
        GodCommand command = new GodCommand(GodCommand.Type.ADD, type);
        this.connection.remoteInvoke(command);
    }

    @Override
    public void removeAvailableGod(GodType type)
            throws NotExecutedException, IOException {
        GodCommand command = new GodCommand(GodCommand.Type.REMOVE, type);
        this.connection.remoteInvoke(command);
    }

    @Override
    public void nextTurn() throws NotExecutedException, IOException {
        MiscellaneousCommand command =
                new MiscellaneousCommand(MiscellaneousCommand.Type.NEXT_TURN);
        this.connection.remoteInvoke(command);
    }

    @Override
    public void setCurrentPlayer(int index)
            throws NotExecutedException, IOException {
        SetCurrentPlayerCommand command = new SetCurrentPlayerCommand(index);
        this.connection.remoteInvoke(command);
    }

    @Override
    public void setGameStatus(GameStatus status)
            throws NotExecutedException, IOException {
        GameStatusCommand command = new GameStatusCommand(status);
        this.connection.remoteInvoke(command);
    }

    @Override
    public void setPlayerGod(String player, GodType god)
            throws NotExecutedException, IOException {
        ChooseGodCommand command = new ChooseGodCommand(player, god);
        this.connection.remoteInvoke(command);
    }

    @Override
    public void selectWorker(int index) throws NotExecutedException, IOException {
        SelectWorkerCommand command = new SelectWorkerCommand(index);
        this.connection.remoteInvoke(command);
    }

    @Override
    public void undo() throws NotExecutedException, IOException {
        MiscellaneousCommand command =
                new MiscellaneousCommand(MiscellaneousCommand.Type.UNDO);
        this.connection.remoteInvoke(command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<WorkerActionType, List<Vector2>> getValidActions()
            throws NotExecutedException, IOException {
        GetValidActionsCommand command = new GetValidActionsCommand();
        return (Map<WorkerActionType, List<Vector2>>)this.connection.remoteInvoke(command);
    }
}

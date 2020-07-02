package it.polimi.ingsw.controller.game;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.models.game.*;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.views.game.GameView;

import java.io.IOException;

public interface GameController {

    void joinGame(String nickname, GameView view)
            throws NotExecutedException, IOException;

    void selectWorker(int index)
            throws NotExecutedException, IOException;

    void workerAction(String player,
                      WorkerActionType action,
                      int x, int y) throws NotExecutedException, IOException;

    void addAvailableGods(GodType type)
            throws NotExecutedException, IOException;

    void removeAvailableGod(GodType type)
            throws NotExecutedException, IOException;

    void nextTurn()
            throws NotExecutedException, IOException;

    void setCurrentPlayer(int index)
            throws NotExecutedException, IOException;

    /**
     * Set the {@link GameStatus}.
     *
     * @param status the game status to be set.
     */
    void setGameStatus(GameStatus status)
            throws NotExecutedException, IOException;

    void setPlayerGod(String player, GodType god)
            throws NotExecutedException, IOException;



    void resetTurn()
            throws NotExecutedException, IOException;

    void undo()
            throws NotExecutedException, IOException;
}

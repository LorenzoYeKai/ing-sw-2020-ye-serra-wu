package it.polimi.ingsw.views.game;

import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.gods.GodType;

import java.util.Collection;

public interface GameView {
    void notifyGameStatus(GameStatus status);
    void notifyAvailableGods(Collection<GodType> availableGods);
    void notifyPlayerHasGod(String player, GodType playerGod);
    void notifySpaceChange(Space space);
    void notifyPlayerTurn(String player);
    void notifyPlayerDefeat(String player);
}

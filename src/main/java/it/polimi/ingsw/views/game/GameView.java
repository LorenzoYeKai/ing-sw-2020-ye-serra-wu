package it.polimi.ingsw.views.game;

import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.gods.GodType;

import java.util.Collection;
import java.util.Map;

public interface GameView {
    void notifyGameStatus(GameStatus status);
    void notifyAvailableGods(Collection<GodType> availableGods);
    void notifyPlayerGods(Map<String, GodType> playerAndGods);
    void notifySpaceChange(Space space);
    void notifyPlayerTurn(String player);
    void notifyPlayerDefeat(String player);
}

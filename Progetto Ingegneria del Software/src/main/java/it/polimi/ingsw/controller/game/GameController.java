package it.polimi.ingsw.controller.game;

import it.polimi.ingsw.models.game.PlayerData;
import it.polimi.ingsw.models.game.SpaceData;
import it.polimi.ingsw.models.game.WorkerData;
import it.polimi.ingsw.models.lobby.UserData;

public class GameController {
    public GameController() {

    }

    public PlayerData getPlayer(UserData user) {
        throw new RuntimeException("Not implemented yet");
    }

    public void workerAction(WorkerData worker, WorkerActionType action, SpaceData targetSpace) {
        throw new RuntimeException("Not implemented yet");
    }
}

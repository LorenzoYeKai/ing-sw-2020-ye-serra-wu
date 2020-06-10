package it.polimi.ingsw.models.lobby;

import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.views.lobby.LobbyView;

import java.util.Optional;

/**
 * Represents a user in the lobby,
 * Who might become a {@link Player}
 */
public class User {
    private final String name;
    private final LobbyView view;
    private String currentRoomName;

    public User(String name, LobbyView view) {
        this.name = name;
        this.view = view;
        this.currentRoomName = null;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getCurrentRoomName() {
        return Optional.ofNullable(this.currentRoomName);
    }

    public void setCurrentRoomName(String roomName) {
        this.currentRoomName = roomName;
        this.getView().notifyRoomChanged(roomName);
    }

    public LobbyView getView() {
        return this.view;
    }


}
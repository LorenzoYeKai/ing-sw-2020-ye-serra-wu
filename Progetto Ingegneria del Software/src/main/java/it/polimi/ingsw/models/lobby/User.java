package it.polimi.ingsw.models.lobby;

import it.polimi.ingsw.models.game.Player;
import it.polimi.ingsw.views.lobby.LobbyView;

/**
 * Represents a user in the lobby,
 * Who might become a {@link Player}
 */
public class User implements UserData {
    private final String username;
    private final LobbyView view;
    private RoomData currentRoom;

    public User(String username, LobbyView view) {
        this.username = username;
        this.view = view;
        this.currentRoom = null;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public RoomData getCurrentRoom() {
        return this.currentRoom;
    }

    public void setCurrentRoom(RoomData room) {
        this.currentRoom = room;
    }

    public LobbyView getView() {
        return this.view;
    }

}
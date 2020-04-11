package it.polimi.ingsw.controller.lobby;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.lobby.*;
import it.polimi.ingsw.views.lobby.LobbyView;

import java.util.ArrayList;
import java.util.List;

public class LobbyController {
    private final Lobby lobby;

    public LobbyController() {
        this.lobby = new Lobby();
    }

    /**
     * Join the lobby with a username.
     * An {@link User} will be created with the specified username and view.
     * Event listeners will automatically be set up.
     *
     * @param username The desired username
     * @param view     The {@link LobbyView} used by the current "pre-user".
     * @return The created {@link User}
     * @throws NotExecutedException If the desired username is already been used.
     */
    public UserData joinLobby(String username, LobbyView view) throws NotExecutedException {
        if (this.lobby.getUser(username) != null) {
            throw new NotExecutedException("Username already taken");
        }

        User newUser = new User(username, view);
        this.lobby.addUser(newUser);
        return newUser;
    }

    public void leaveLobby(UserData userData) throws NotExecutedException {
        this.throwIfUserNotInLobby(userData);

        User user = this.lobby.getUser(userData.getUsername());
        this.lobby.removeUser(user);
    }

    public void createRoom(UserData userData, String roomName) throws NotExecutedException {
        this.throwIfUserNotInLobby(userData);
        this.throwIfAlreadyInRoom(userData);

        User host = this.lobby.getUser(userData.getUsername());
        Room room = new Room(this.lobby, host, roomName);
        this.lobby.addRoom(room);
    }

    public void joinRoom(UserData userData, RoomData roomData) throws NotExecutedException {
        this.throwIfUserNotInLobby(userData);
        this.throwIfAlreadyInRoom(userData);
        this.throwIfRoomNotInLobby(roomData);

        User user = this.lobby.getUser(userData.getUsername());
        Room room = this.lobby.getRoom(roomData.getRoomId());
        room.join(user);
    }

    /**
     * Make some user to leave the room, with an optional message.
     * If host has left room, everybody leaves too.
     *
     * @param user     The user who will leave the room
     * @param roomData The room from which the user wants to leave.
     * @throws NotExecutedException If the user is not in the room
     */
    public void leaveRoom(UserData user, RoomData roomData) throws NotExecutedException {
        this.throwIfUserNotInLobby(user);
        this.throwIfRoomNotInLobby(roomData);
        this.throwIfNotInRoom(user, roomData);

        Room room = this.lobby.getRoom(roomData.getRoomId());
        room.leave(user);
    }

    public void changePlayerPosition(UserData host,
                                     RoomData roomData,
                                     UserData target,
                                     int offset) throws NotExecutedException {
        this.throwIfUserNotInLobby(host);
        this.throwIfUserNotInLobby(target);
        this.throwIfRoomNotInLobby(roomData);
        this.throwIfNotInRoom(host, roomData);
        this.throwIfNotHost(host);
        this.throwIfNotInRoom(host, roomData);

        Room room = this.lobby.getRoom(roomData.getRoomId());
        room.moveUser(target, offset);
    }

    public void kickUser(UserData host, RoomData roomData, UserData target) throws NotExecutedException {
        this.throwIfUserNotInLobby(host);
        this.throwIfUserNotInLobby(target);
        this.throwIfRoomNotInLobby(roomData);
        this.throwIfNotInRoom(host, roomData);
        this.throwIfNotHost(host);
        this.throwIfNotInRoom(target, roomData);
        if (host == target) {
            throw new NotExecutedException("Cannot kick yourself, leave instead");
        }

        Room room = this.lobby.getRoom(roomData.getRoomId());
        room.kick(target);
    }

    public void startGame(UserData host, RoomData roomData) throws NotExecutedException {
        this.throwIfUserNotInLobby(host);
        this.throwIfRoomNotInLobby(roomData);
        this.throwIfNotInRoom(host, roomData);
        this.throwIfNotHost(host);

        Room room = this.lobby.getRoom(roomData.getRoomId());
        GameController gameController = new GameController();
        // make a temporary copy because we are editing the original list when iterating
        room.startGame(gameController);
    }

    private void throwIfUserNotInLobby(UserData user) throws NotExecutedException {
        if (this.lobby.getUser(user.getUsername()) != user) {
            throw new NotExecutedException("Username does not exist");
        }
    }

    private void throwIfRoomNotInLobby(RoomData room) throws NotExecutedException {
        if (this.lobby.getRoom(room.getRoomId()) != room) {
            throw new NotExecutedException("Room not in lobby");
        }
    }

    private void throwIfAlreadyInRoom(UserData userData) throws NotExecutedException {
        User user = this.lobby.getUser(userData.getUsername());
        if (user.getCurrentRoom() != null) {
            throw new NotExecutedException("Already in another room");
        }
    }

    private void throwIfNotInRoom(UserData userData, RoomData room) throws NotExecutedException {
        User user = this.lobby.getUser(userData.getUsername());
        if (user.getCurrentRoom() != room) {
            throw new NotExecutedException("Not inside the room");
        }
    }

    private void throwIfNotHost(UserData userData) throws NotExecutedException {
        User user = this.lobby.getUser(userData.getUsername());
        if (user.getCurrentRoom().getHost() != user) {
            throw new NotExecutedException("You are not host");
        }
    }
}

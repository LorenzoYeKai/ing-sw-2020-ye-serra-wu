package it.polimi.ingsw.controller.lobby;

import it.polimi.ingsw.model.Game;
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
        if (this.lobby.usernameExists(username)) {
            throw new NotExecutedException("Username already taken");
        }

        User newUser = new User(username, view);
        this.lobby.addListeners(newUser,
                users -> newUser.getView().displayUserList(users),
                rooms -> newUser.getView().displayAvailableRooms(rooms));
        this.lobby.addUser(newUser);
        return newUser;
    }

    public void leaveLobby(UserData userData) throws NotExecutedException {
        this.throwIfNotInLobby(userData);

        User user = this.lobby.getUser(userData.getUsername());
        user.getView().displayLastMessage("SYSTEM", "You are leaving the lobby");
        this.lobby.removeListeners(user);
        this.lobby.removeUser(user);

    }

    public void createRoom(UserData userData, String roomName) throws NotExecutedException {
        this.throwIfNotInLobby(userData);
        this.throwIfAlreadyInRoom(userData);

        User host = this.lobby.getUser(userData.getUsername());
        Room room = new Room(this.lobby, host, roomName);
        host.setCurrentRoom(room);
        host.getView().notifyHostedRoom(room);
        room.addListener(host, users -> host.getView().displayRoomPlayerList(users));
        this.lobby.addRoom(room);
    }

    public void joinRoom(UserData userData, RoomData roomData) throws NotExecutedException {
        this.throwIfNotInLobby(userData);
        this.throwIfAlreadyInRoom(userData);

        User user = this.lobby.getUser(userData.getUsername());
        Room room = this.lobby.getRoom(roomData.getRoomId());
        user.setCurrentRoom(room);
        user.getView().notifyJoinedRoom(room);
        room.addListener(user, players -> user.getView().displayRoomPlayerList(players));
        room.add(user);
    }

    /**
     * Leave the room. If host has left room, everybody leaves too.
     *
     * @param userData The user who wants to leave.
     * @throws NotExecutedException If the user is not in the room
     */
    public void leaveRoom(UserData userData) throws NotExecutedException {
        this.leaveRoom(userData, null);
    }

    /**
     * Make some user to leave the room, with an optional message.
     * If host has left room, everybody leaves too.
     *
     * @param userData The user who will leave the room
     * @param message  The message which will be sent to the user, if not null
     * @throws NotExecutedException If the user is not in the room
     */
    private void leaveRoom(UserData userData, String message) throws NotExecutedException {
        this.throwIfNotInLobby(userData);
        this.throwIfNotInRoom(userData);

        User user = this.lobby.getUser(userData.getUsername());
        Room room = this.lobby.getRoom(user.getCurrentRoom().getRoomId());

        // If host leaves, then everyone else leaves too
        if (room.getHost() == user) {
            // make a temporary copy because we are editing the original list when iterating
            List<UserData> users = new ArrayList<>(room.getUsers());
            for (UserData member : users) {
                if (member == user) {
                    continue;
                }
                this.leaveRoom(member, "Host has left the game");
            }
        }

        if (message != null) {
            user.getView().displayLastMessage("SYSTEM", message);
        }
        user.setCurrentRoom(null);
        user.getView().notifyLeftRoom(room);
        room.removeListener(user);
        room.remove(user.getUsername());
    }

    public void changePlayerPosition(UserData host, UserData target, int offset) throws NotExecutedException {
        this.throwIfNotInLobby(host);
        this.throwIfNotInLobby(target);
        this.throwIfNotInRoom(host);
        this.throwIfNotHost(host);
        if (host.getCurrentRoom() != target.getCurrentRoom()) {
            throw new NotExecutedException("Not in the same room");
        }

        Room room = this.lobby.getRoom(host.getCurrentRoom().getRoomId());
        room.moveUser(target, offset);
    }

    public void kickUser(UserData host, UserData target) throws NotExecutedException {
        this.throwIfNotInLobby(host);
        this.throwIfNotInLobby(target);
        this.throwIfNotInRoom(host);
        this.throwIfNotHost(host);
        if (host.getCurrentRoom() != target.getCurrentRoom()) {
            throw new NotExecutedException("Not in the same room");
        }

        Room room = this.lobby.getRoom(host.getCurrentRoom().getRoomId());
        this.leaveRoom(target, "You have been kicked");
    }

    public void startGame(UserData host) throws NotExecutedException {
        this.throwIfNotInLobby(host);
        this.throwIfNotInRoom(host);
        this.throwIfNotHost(host);

        Room room = this.lobby.getRoom(host.getCurrentRoom().getRoomId());
        GameController gameController = new GameController();
        // make a temporary copy because we are editing the original list when iterating
        List<UserData> users = new ArrayList<>(room.getUsers());
        for (UserData user : users) {
            if(user == host) {
                continue; // Host must leave after everyone else has left
            }

            User player = this.lobby.getUser(user.getUsername());
            player.getView().notifyGameStarted(gameController);
            this.leaveRoom(player, "Game is starting...");
            this.leaveLobby(player);
        }

        User hostPlayer = this.lobby.getUser(host.getUsername());
        hostPlayer.getView().notifyGameStarted(gameController);
        this.leaveRoom(hostPlayer);
        this.leaveLobby(hostPlayer);
    }

    private void throwIfNotInLobby(UserData user) throws NotExecutedException {
        if (!this.lobby.usernameExists(user.getUsername())) {
            throw new NotExecutedException("Username does not exist");
        }
    }

    private void throwIfAlreadyInRoom(UserData user) throws NotExecutedException {
        if (user.getCurrentRoom() != null) {
            throw new NotExecutedException("Already in another room");
        }
    }

    private void throwIfNotInRoom(UserData user) throws NotExecutedException {
        if (user.getCurrentRoom() == null) {
            throw new NotExecutedException("Not inside any room");
        }
    }

    private void throwIfNotHost(UserData user) throws NotExecutedException {
        if (user.getCurrentRoom().getHost() != user) {
            throw new NotExecutedException("You are not host");
        }
    }
}

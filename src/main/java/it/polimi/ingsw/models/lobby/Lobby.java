package it.polimi.ingsw.models.lobby;

import it.polimi.ingsw.models.InternalError;
import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.Notifier;
import it.polimi.ingsw.views.lobby.LobbyView;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a lobby where {@link User}s can communicate
 * and can create new {@link Game}s.
 */
public class Lobby {
    private final Notifier<Collection<String>> usersChangedNotifier;
    private final Notifier<Collection<String>> roomsChangedNotifier;
    private final Map<String, User> users;
    private final Map<UserToken, User> usersByToken;
    private final Map<String, Room> stagingRooms;

    public Lobby() {
        this.usersChangedNotifier = new Notifier<>();
        this.roomsChangedNotifier = new Notifier<>();
        this.users = new HashMap<>();
        this.usersByToken = new HashMap<>();
        this.stagingRooms = new HashMap<>();
    }

    public Optional<User> getUser(String userName) {
        if (!this.users.containsKey(userName)) {
            return Optional.empty();
        }
        return Optional.of(this.users.get(userName));
    }

    public Optional<User> getUser(UserToken userToken) {
        if (!this.usersByToken.containsKey(userToken)) {
            return Optional.empty();
        }
        return Optional.of(this.usersByToken.get(userToken));
    }

    public UserToken createUser(String userName, LobbyView view) {
        if (this.users.containsKey(userName)) {
            throw new InternalError("Username already exists");
        }

        User user = new User(userName, view);
        UserToken token = new UserToken();
        this.users.put(userName, user);
        this.usersByToken.put(token, user);
        this.addListeners(user,
                users -> user.getView().displayUserList(users),
                rooms -> user.getView().displayAvailableRooms(rooms));
        // notify all users (including the new user) about the updated player list
        this.usersChangedNotifier.notify(Collections.unmodifiableCollection(this.users.keySet()));
        // notify (only) the new user about the room list
        user.getView().displayAvailableRooms(Collections.unmodifiableCollection(this.stagingRooms.keySet()));
        return token;
    }

    public void removeUser(UserToken userToken) {
        if (!this.usersByToken.containsKey(userToken)) {
            throw new InternalError("User token does not exist");
        }

        User user = this.usersByToken.get(userToken);
        // in exceptional situations, user might leave the lobby even when inside a room
        // then let him leave the room as well.
        user.getCurrentRoomName().flatMap(this::getRoom).ifPresent(room -> room.leave(user));

        user.getView().notifyMessage("SYSTEM", "You are leaving the lobby");
        this.users.remove(user.getName());
        this.usersByToken.remove(userToken);
        this.removeListeners(user);
        this.usersChangedNotifier.notify(Collections.unmodifiableCollection(this.users.keySet()));
    }

    public Optional<Room> getRoom(String roomName) {
        if (!this.stagingRooms.containsKey(roomName)) {
            return Optional.empty();
        }
        return Optional.of(this.stagingRooms.get(roomName));
    }

    public Room createRoom(UserToken hostToken) {
        if (!this.usersByToken.containsKey(hostToken)) {
            throw new InternalError("User token does not exist");
        }

        User user = this.usersByToken.get(hostToken);
        if (user.getCurrentRoomName().isPresent()) {
            throw new InternalError("User is already inside a room");
        }

        Room room = new Room(this, user);
        if (this.stagingRooms.containsKey(room.getName())) {
            throw new InternalError("Room already exists");
        }
        this.stagingRooms.put(room.getName(), room);
        this.roomsChangedNotifier.notify(Collections.unmodifiableCollection(this.stagingRooms.keySet()));
        return room;
    }

    public void removeRoom(Room room) {
        this.stagingRooms.remove(room.getName());
        this.roomsChangedNotifier.notify(Collections.unmodifiableCollection(this.stagingRooms.keySet()));
    }

    private void addListeners(Object key,
                              Consumer<Collection<String>> onUsersChanged,
                              Consumer<Collection<String>> onRoomsChanged) {
        this.usersChangedNotifier.addListener(key, onUsersChanged);
        this.roomsChangedNotifier.addListener(key, onRoomsChanged);
    }

    private void removeListeners(Object key) {
        this.usersChangedNotifier.removeListener(key);
        this.roomsChangedNotifier.removeListener(key);
    }
}
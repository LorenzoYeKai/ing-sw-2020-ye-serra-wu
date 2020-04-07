package it.polimi.ingsw.models.lobby;

import it.polimi.ingsw.models.game.Game;
import it.polimi.ingsw.Notifier;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a lobby where {@link User}s can communicate
 * and can create new {@link Game}s.
 */
public class Lobby {
    private final Notifier<Set<String>> usersChangedNotifier;
    private final Notifier<Collection<RoomData>> roomsChangedNotifier;
    private final Map<String, User> users;
    private final Map<Integer, Room> stagingRooms;

    public Lobby() {
        this.usersChangedNotifier = new Notifier<>();
        this.roomsChangedNotifier = new Notifier<>();
        this.users = new HashMap<>();
        this.stagingRooms = new HashMap<>();
    }

    public void addListeners(Object key,
                             Consumer<Set<String>> onUsersChanged,
                             Consumer<Collection<RoomData>> onRoomsChanged) {
        this.usersChangedNotifier.addListener(key, onUsersChanged);
        this.roomsChangedNotifier.addListener(key, onRoomsChanged);
    }

    public void removeListeners(Object key) {
        this.usersChangedNotifier.removeListener(key);
        this.roomsChangedNotifier.removeListener(key);
    }

    public boolean usernameExists(String username) {
        return this.users.containsKey(username);
    }

    public User getUser(String username) {
        return this.users.get(username);
    }

    public void addUser(User user) {
        this.users.put(user.getUsername(), user);
        this.usersChangedNotifier.notify(Collections.unmodifiableSet(this.users.keySet()));
    }

    public void removeUser(User user) {
        this.users.remove(user.getUsername());
        this.usersChangedNotifier.notify(Collections.unmodifiableSet(this.users.keySet()));
    }

    public Room getRoom(int roomId) {
        return this.stagingRooms.get(roomId);
    }

    public void addRoom(Room room) {
        this.stagingRooms.put(room.getRoomId(), room);
        this.roomsChangedNotifier.notify(Collections.unmodifiableCollection(this.stagingRooms.values()));
    }

    public void removeRoom(Room room) {
        this.stagingRooms.remove(room.getRoomId());
        this.roomsChangedNotifier.notify(Collections.unmodifiableCollection(this.stagingRooms.values()));
    }
}
package it.polimi.ingsw.models.lobby;

import it.polimi.ingsw.Notifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Room implements RoomData {
    private static int lastRoomId;
    private final Lobby lobby;
    private final Notifier<List<UserData>> usersChangedNotifier;
    private final int roomId;
    private final String roomName;
    private final UserData host;
    private final List<UserData> allUsers;


    public Room(Lobby lobby, UserData host, String roomName) {
        this.lobby = lobby;
        this.usersChangedNotifier = new Notifier<>();
        this.roomId = ++lastRoomId;
        this.roomName = roomName;
        this.host = host;
        this.allUsers = new ArrayList<>();
        allUsers.add(host);
    }

    public void addListener(Object key, Consumer<List<UserData>> onUsersChanged) {
        this.usersChangedNotifier.addListener(key, onUsersChanged);
    }

    public void removeListener(Object key) {
        this.usersChangedNotifier.removeListener(key);
    }

    @Override
    public int getRoomId() {
        return this.roomId;
    }

    @Override
    public String getRoomName() {
        return this.roomName;
    }

    @Override
    public UserData getHost() {
        return this.host;
    }

    @Override
    public int getNumberOfUsers() {
        return this.allUsers.size();
    }


    public List<UserData> getUsers() {
        return Collections.unmodifiableList(this.allUsers);
    }

    /**
     * Add a user to the player list.
     *
     * @param user the user to be added
     */
    public void add(User user) {
        this.allUsers.add(user);
        this.usersChangedNotifier.notify(this.getUsers());
    }

    /**
     * Remove a user from player list.
     * If the last user has left the room, this room is removed from lobby.
     *
     * @param username the name of user who left.
     */
    public void remove(String username) {
        this.allUsers.removeIf(x -> x.getUsername().equals(username));
        if (this.allUsers.isEmpty()) {
            this.lobby.removeRoom(this);
        } else {
            this.usersChangedNotifier.notify(this.getUsers());
        }
    }

    /**
     * Move the user in the player list
     *
     * @param user   the user to be moved
     * @param offset number of positions to offset
     */
    public void moveUser(UserData user, int offset) {
        // Find the user with matching username
        OptionalInt findResult = IntStream.range(0, this.allUsers.size())
                .filter(i -> this.allUsers.get(i) == user)
                .findFirst();
        if (findResult.isEmpty()) {
            throw new RuntimeException();
        }
        final int userIndex = findResult.getAsInt();

        // Calculate destination position
        int destinationIndex = userIndex + offset;
        destinationIndex = Math.max(0, Math.min(destinationIndex, this.allUsers.size()));
        if (userIndex == destinationIndex) {
            // same position, no move required
            return;
        }

        UserData movingUser = this.allUsers.get(userIndex);
        this.allUsers.set(userIndex, this.allUsers.get(destinationIndex));
        this.allUsers.set(destinationIndex, movingUser);

        this.usersChangedNotifier.notify(this.getUsers());
    }

}

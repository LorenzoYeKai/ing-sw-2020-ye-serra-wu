package it.polimi.ingsw.models.lobby;

import it.polimi.ingsw.Notifier;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.InternalError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Room implements RoomData {
    private static int lastRoomId;
    private final Lobby lobby;
    private final Notifier<List<UserData>> usersChangedNotifier;
    private final int roomId;
    private final String roomName;
    private final UserData host;
    private final List<User> allUsers;

    /**
     * Creates a new room in the lobby
     *
     * @param lobby    the lobby from which the room is created.
     * @param host     the {@link User} who created this room.
     * @param roomName the name fo this room.
     */
    public Room(Lobby lobby, User host, String roomName) {
        this.lobby = lobby;
        this.usersChangedNotifier = new Notifier<>();
        this.roomId = ++lastRoomId;
        this.roomName = roomName;
        this.host = host;
        this.allUsers = new ArrayList<>();

        this.join(host);
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

    public List<UserData> getUsers() {
        return Collections.unmodifiableList(this.allUsers);
    }

    /**
     * Called to make someone join the room.
     *
     * @param user the user who is joining
     */
    public void join(User user) {
        this.allUsers.add(user);
        user.setCurrentRoom(this);
        this.addListener(user, players -> user.getView().displayRoomPlayerList(players));
        this.usersChangedNotifier.notify(this.getUsers());
    }

    /**
     * Called let someone leaving the room.
     *
     * @param user the user who is leaving
     */
    public void leave(UserData user) {
        if (user == this.getHost()) {
            // make a temporary copy of the list because the
            // original list might be edited when iterating
            for (User member : new ArrayList<>(this.allUsers)) {
                // host must leave after everyone else has left
                if (member == user) {
                    continue;
                }

                this.remove(member, "Host has left the room");
            }
        }

        this.remove(user, null);
    }

    /**
     * Called to kick someone from the room.
     *
     * @param user the user who needs to be kicked
     */
    public void kick(UserData user) {
        if (user == this.getHost()) {
            throw new InternalError("Cannot kick yourself");
        }

        this.remove(user, "You have been kicked");
    }

    /**
     * Remove a user from player list.
     * If this room becomes empty, it's removed from the {@link Lobby}.
     *
     * @param userData the user who wants to leave the room.
     */
    private void remove(UserData userData, String message) {
        //noinspection SuspiciousMethodCalls
        int index = this.allUsers.indexOf(userData);
        if (index == -1) {
            throw new InternalError("User not in this room");
        }
        User user = this.allUsers.get(index);

        if (message != null) {
            user.getView().notifyMessage("SYSTEM", message);
        }
        this.allUsers.remove(user);
        user.setCurrentRoom(null);
        this.removeListener(user);
        this.usersChangedNotifier.notify(this.getUsers());
        if (this.allUsers.isEmpty()) {
            this.lobby.removeRoom(this);
        }
    }

    /**
     * Move the user in the player list
     *
     * @param user   the user to be moved
     * @param offset number of positions to offset
     */
    public void moveUser(UserData user, int offset) {
        //noinspection SuspiciousMethodCalls
        int userIndex = this.allUsers.indexOf(user);
        if (userIndex == -1) {
            throw new InternalError("User not in room");
        }

        // Calculate destination position
        int destinationIndex = userIndex + offset;
        destinationIndex = Math.max(0, Math.min(destinationIndex, this.allUsers.size() - 1));
        if (userIndex == destinationIndex) {
            // same position, no move required
            return;
        }

        User movingUser = this.allUsers.get(userIndex);
        this.allUsers.set(userIndex, this.allUsers.get(destinationIndex));
        this.allUsers.set(destinationIndex, movingUser);

        this.usersChangedNotifier.notify(this.getUsers());
    }

    /**
     * Notify everyone that the game is starting,
     * then make everyone leave the room
     *
     * @param gameController the newly-created game controller.
     */
    public void startGame(GameController gameController) {
        // make a temporary copy of the list because the
        // original list might be edited when iterating
        for (User member : new ArrayList<>(this.allUsers)) {
            member.getView().notifyGameStarted(gameController);
        }

        this.leave(this.getHost());
    }

    private void addListener(Object key, Consumer<List<UserData>> onUsersChanged) {
        this.usersChangedNotifier.addListener(key, onUsersChanged);
    }

    private void removeListener(Object key) {
        this.usersChangedNotifier.removeListener(key);
    }
}

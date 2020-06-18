package it.polimi.ingsw.models.lobby;

import it.polimi.ingsw.Notifier;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.InternalError;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Room {
    private final Lobby lobby;
    private final Notifier<List<String>> usersChangedNotifier;
    private final User host;
    private final List<User> users;

    /**
     * Creates a new room in the lobby
     *
     * @param lobby the lobby from which the room is created.
     * @param host  the {@link User} who created this room.
     */
    public Room(Lobby lobby, User host) {
        this.lobby = lobby;
        this.usersChangedNotifier = new Notifier<>();
        this.host = host;
        this.users = new ArrayList<>();
        this.join(host);
    }

    public String getName() {
        return this.host.getName();
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(this.users);
    }

    /**
     * Make someone join the room.
     *
     * @param user the user who is joining
     */
    public void join(User user) {
        this.users.add(user);
        user.setCurrentRoomName(this.getName());
        this.addListener(user, players -> user.getView().displayRoomPlayerList(players));
        this.notifyUsersChanged();
    }

    /**
     * Make someone to leave the room.
     *
     * @param user the user who is leaving
     */
    public void leave(User user) {
        // if host leaves the room, everyone leaves too
        if (user == this.host) {
            // create a copy of list because we are mutating the list
            // while iterating over it
            for (User member : new ArrayList<>(this.users)) {
                // host must leave after everyone else has left
                if (user == member) {
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
    public void kick(User user) {
        if (user == this.host) {
            throw new InternalError("Cannot kick yourself");
        }

        this.remove(user, "You have been kicked");
    }

    /**
     * Move the user in the player list
     *
     * @param user the user to be moved
     * @param offset   number of positions to offset
     */
    public void moveUser(User user, int offset) {
        int userIndex = this.users.indexOf(user);
        if (userIndex == -1) {
            throw new InternalError("User not in room");
        }

        // Calculate destination position
        int destinationIndex = userIndex + offset;
        destinationIndex = Math.max(0, Math.min(destinationIndex, this.users.size() - 1));
        if (userIndex == destinationIndex) {
            // same position, no move required
            return;
        }

        User movingUser = this.users.get(userIndex);
        this.users.set(userIndex, this.users.get(destinationIndex));
        this.users.set(destinationIndex, movingUser);

        this.notifyUsersChanged();
    }

    /**
     * Notify everyone that the game is starting,
     * then make everyone leave the room
     *
     * @param gameController the newly-created game controller.
     */
    public void startGame(GameController gameController) {
        for (User member : this.users) {
            member.getView().notifyGameStarted(gameController);
        }

        // let host (so everyone) leave the room
        this.leave(this.host);
    }

    /**
     * Remove a user from player list.
     * If this room becomes empty, it's removed from the {@link Lobby}.
     *
     * @param user the user who wants to leave the room.
     */
    private void remove(User user, String message) {
        int index = this.users.indexOf(user);
        if(index == -1) {
            throw new InternalError("User not in room");
        }

        if (message != null) {
            user.getView().notifyMessage("SYSTEM", message);
        }
        this.users.remove(user);
        this.notifyUsersChanged();
        user.setCurrentRoomName(null);
        this.removeListener(user);
        if (this.users.isEmpty()) {
            this.lobby.removeRoom(this);
        }
    }

    private void addListener(Object key, Consumer<List<String>> onUsersChanged) {
        this.usersChangedNotifier.addListener(key, onUsersChanged);
    }

    private void removeListener(Object key) {
        this.usersChangedNotifier.removeListener(key);
    }

    private void notifyUsersChanged(){
        this.usersChangedNotifier.notify(this.users.stream()
                .map(User::getName)
                .collect(Collectors.toList()));
    }
}

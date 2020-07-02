package it.polimi.ingsw.controller.lobby;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.LocalGameController;
import it.polimi.ingsw.models.lobby.*;
import it.polimi.ingsw.views.lobby.LobbyView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalLobbyController implements LobbyController {
    private final Lobby lobby;

    public LocalLobbyController() {
        this.lobby = new Lobby();
    }

    @Override
    public synchronized UserToken joinLobby(String username, LobbyView view)
            throws NotExecutedException {
        if (this.lobby.getUser(username).isPresent()) {
            throw new NotExecutedException("Username already taken");
        }

        return this.lobby.createUser(username, view);
    }

    @Override
    public synchronized void leaveLobby(UserToken userToken)
            throws NotExecutedException {
        this.ensureLobbyHasUser(userToken);

        this.lobby.removeUser(userToken);
    }

    @Override
    public synchronized void createRoom(UserToken userToken)
            throws NotExecutedException {
        User user = this.ensureLobbyHasUser(userToken);
        this.throwIfAlreadyInRoom(user);

        this.lobby.createRoom(userToken);
    }

    @Override
    public synchronized void joinRoom(UserToken userToken, String roomName)
            throws NotExecutedException {
        User user = this.ensureLobbyHasUser(userToken);
        Room room = this.ensureRoomInLobby(roomName);
        this.throwIfAlreadyInRoom(user);

        room.join(user);
    }

    @Override
    public synchronized void leaveRoom(UserToken token)
            throws NotExecutedException {
        User user = this.ensureLobbyHasUser(token);
        Optional<String> currentRoomName = user.getCurrentRoomName();
        if (currentRoomName.isEmpty()) {
            throw new NotExecutedException("User not in any room");
        }
        Room room = this.ensureRoomInLobby(currentRoomName.get());

        room.leave(user);
    }

    @Override
    public synchronized void changePlayerPosition(UserToken hostToken,
                                                  String targetUserName,
                                                  int offset)
            throws NotExecutedException {
        User target = this.ensureLobbyHasUser(targetUserName);
        Room room = this.ensureTargetInHostRoom(hostToken, target);

        room.moveUser(target, offset);
    }

    @Override
    public synchronized void kickUser(UserToken hostToken, String targetUserName)
            throws NotExecutedException {
        User host = this.ensureLobbyHasUser(hostToken);
        User target = this.ensureLobbyHasUser(targetUserName);
        if (host.equals(target)) {
            throw new NotExecutedException("Cannot kick yourself, leave instead");
        }

        Room room = this.ensureTargetInHostRoom(hostToken, target);
        room.kick(target);
    }

    @Override
    public synchronized void startGame(UserToken hostToken)
            throws NotExecutedException {
        Room room = this.ensureUserHostedRoom(hostToken);
        List<String> nameList = room.getUsers().stream()
                .map(User::getName)
                .collect(Collectors.toList());
        if (nameList.size() < 2) {
            throw new NotExecutedException("Need at least 2 players");
        }

        GameController gameController = new LocalGameController(nameList);
        room.startGame(gameController);
    }

    private User ensureLobbyHasUser(UserToken token) throws NotExecutedException {
        Optional<User> maybeUser = this.lobby.getUser(token);
        if (maybeUser.isEmpty()) {
            throw new NotExecutedException("User does not exist");
        }
        return maybeUser.get();
    }

    private User ensureLobbyHasUser(String userName) throws NotExecutedException {
        Optional<User> maybeUser = this.lobby.getUser(userName);
        if (maybeUser.isEmpty()) {
            throw new NotExecutedException("User does not exist");
        }
        return maybeUser.get();
    }

    private Room ensureUserHostedRoom(UserToken hostToken) throws NotExecutedException {
        User host = this.ensureLobbyHasUser(hostToken);
        Optional<String> currentHostedRoomName = host.getCurrentRoomName();
        if (currentHostedRoomName.isEmpty()) {
            throw new NotExecutedException("Host not in any room");
        }
        if (!currentHostedRoomName.get().equals(host.getName())) {
            throw new NotExecutedException("Host not in his room");
        }
        return this.ensureRoomInLobby(host.getName());
    }

    private Room ensureTargetInHostRoom(UserToken hostToken, User targetUser) throws NotExecutedException {
        Room room = this.ensureUserHostedRoom(hostToken);
        Optional<String> targetUserCurrentRoomName = targetUser.getCurrentRoomName();
        if (targetUserCurrentRoomName.isEmpty()) {
            throw new NotExecutedException("Target user not in any room");
        }
        if (!targetUserCurrentRoomName.get().equals(room.getName())) {
            throw new NotExecutedException("Target user not in this room");
        }
        return room;
    }

    private Room ensureRoomInLobby(String roomName) throws NotExecutedException {
        Optional<Room> maybeRoom = this.lobby.getRoom(roomName);
        if (maybeRoom.isEmpty()) {
            throw new NotExecutedException("Room not in lobby");
        }
        return maybeRoom.get();
    }

    private void throwIfAlreadyInRoom(User user) throws NotExecutedException {
        if (user.getCurrentRoomName().isPresent()) {
            throw new NotExecutedException("Already in another room");
        }
    }
}

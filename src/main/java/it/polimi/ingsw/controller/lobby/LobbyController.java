package it.polimi.ingsw.controller.lobby;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.models.lobby.*;
import it.polimi.ingsw.views.lobby.LobbyView;


public interface LobbyController {

    /**
     * Join the lobby with a username.
     * An {@link User} will be created with the specified username and view.
     * Event listeners will automatically be set up.
     *
     * @param username The desired username.
     * @param view     The {@link LobbyView} used by the current "pre-user".
     * @return a private {@link UserToken} which uniquely identifies the user
     * @throws NotExecutedException If the desired username is already been used.
     */
    UserToken joinLobby(String username, LobbyView view) throws NotExecutedException;

    /**
     * Leave the lobby.
     *
     * @param userToken The token of user who wants to leave the lobby.
     * @throws NotExecutedException If user cannot leave from the lobby.
     */
    void leaveLobby(UserToken userToken) throws NotExecutedException;

    /**
     * Create a room.
     *
     * @param userToken The token of user who wants to create a room.
     * @throws NotExecutedException If user cannot create this room.
     */
    void createRoom(UserToken userToken) throws NotExecutedException;

    /**
     * Join a room.
     *
     * @param userToken The token of user who wants to join a room.
     * @param roomName  The name of room which the user wants to join.
     * @throws NotExecutedException If user cannot join this room.
     */
    void joinRoom(UserToken userToken, String roomName) throws NotExecutedException;

    /**
     * Leave the room.
     * If host has left room, everybody leaves too.
     *
     * @param userToken The token of user who wants leave the room.
     * @throws NotExecutedException If the user cannot leave this room.
     */
    void leaveRoom(UserToken userToken) throws NotExecutedException;

    /**
     * Change the position of a player inside the player list.
     *
     * @param hostToken      The token of host. Only host can change player position.
     * @param targetUserName The name of target user.
     * @param offset         The offset (in the player list) to apply.
     * @throws NotExecutedException If the target player cannot be moved.
     */
    void changePlayerPosition(UserToken hostToken,
                              String targetUserName,
                              int offset) throws NotExecutedException;

    /**
     * Kick the target player from the room.
     *
     * @param hostToken      The token of host. Only host can kick players.
     * @param targetUserName The victim to be kicked.
     * @throws NotExecutedException If the target player cannot be kicked
     */
    void kickUser(UserToken hostToken, String targetUserName) throws NotExecutedException;

    /**
     * Start the game with players inside the room.
     *
     * @param hostToken The token of host. Only host can start the game.
     * @throws NotExecutedException If game cannot be started from this room.
     */
    void startGame(UserToken hostToken) throws NotExecutedException;
}

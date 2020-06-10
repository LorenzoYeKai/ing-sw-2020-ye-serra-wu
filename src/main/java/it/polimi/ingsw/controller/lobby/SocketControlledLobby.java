package it.polimi.ingsw.controller.lobby;

/**
 * The {@link SocketControlledLobby} accepts inputs from ClientLobbyController by
 * using sockets, and forwards them to the underlying {@link LobbyController}
 */
public class SocketControlledLobby {
    private final LobbyController controller;

    public SocketControlledLobby(LobbyController underlyingController) {
        this.controller = underlyingController;
        
    }
}

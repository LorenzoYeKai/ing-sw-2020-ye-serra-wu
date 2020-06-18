package it.polimi.ingsw.LobbyTest;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.controller.lobby.LocalLobbyController;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.views.lobby.LobbyView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class ControllerTests {
    private static class MockView implements LobbyView {
        @Override
        public void displayAvailableRooms(Collection<String> roomNames) {
        }

        @Override
        public void displayUserList(Collection<String> userNames) {
        }

        @Override
        public void notifyMessage(String author, String message) {
        }

        @Override
        public void notifyRoomChanged(String newRoomName) {
        }

        @Override
        public void displayRoomPlayerList(Collection<String> playerList) {
        }

        @Override
        public void notifyGameStarted(GameController gameController) {
        }
    }

    private final LobbyController controller = new LocalLobbyController();

    @Test
    public void testJoinAndLeave() {
        // joining lobby should not fail
        UserToken token = Assertions.assertDoesNotThrow(() ->
                this.controller.joinLobby("test", new MockView()));
        // joining lobby with duplicated name should fail
        Assertions.assertThrows(NotExecutedException.class, () ->
                this.controller.joinLobby("test", new MockView()));
        // leaving lobby should not fail
        Assertions.assertDoesNotThrow(() ->
                this.controller.leaveLobby(token));
        // leaving lobby again should fail
        Assertions.assertThrows(NotExecutedException.class, () ->
                this.controller.leaveLobby(token));
        // joining lobby again with same name should not fail
        Assertions.assertDoesNotThrow(() ->
                this.controller.joinLobby("test", new MockView()));
    }

}

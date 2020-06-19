package it.polimi.ingsw.views.lobby.remote;

import it.polimi.ingsw.views.lobby.LobbyView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

interface Message extends Serializable {
    void apply(LobbyView view);
}

enum StringsCommandType {
    DISPLAY_AVAILABLE_ROOMS,
    DISPLAY_USER_LIST,
    DISPLAY_ROOM_PLAYER_LIST,
}

final class StringsMessage implements Message {
    private final StringsCommandType type;
    private final ArrayList<String> strings;

    public StringsMessage(StringsCommandType type, Collection<String> strings) {
        this.type = type;
        this.strings = new ArrayList<>(strings);
    }

    @Override
    public void apply(LobbyView view) {
        switch (this.type) {
            case DISPLAY_AVAILABLE_ROOMS -> view.displayAvailableRooms(this.strings);
            case DISPLAY_USER_LIST ->  view.displayUserList(this.strings);
            case DISPLAY_ROOM_PLAYER_LIST -> view.displayRoomPlayerList(this.strings);
            default -> throw new IllegalArgumentException("Invalid type " + this.type);
        }
    }
}

final class TextMessage implements Message {
    private final String author;
    private final String message;

    public TextMessage(String author, String message) {
        this.author = author;
        this.message = message;
    }

    @Override
    public void apply(LobbyView view) {
        view.notifyMessage(this.author, this.message);
    }
}

final class RoomChangedMessage implements Message {
    private final String newRoomName;

    public RoomChangedMessage(String newRoomName) {
        this.newRoomName = newRoomName;
    }

    @Override
    public void apply(LobbyView view) {
        view.notifyRoomChanged(this.newRoomName);
    }
}

final class GameStartedMessage implements Serializable { }
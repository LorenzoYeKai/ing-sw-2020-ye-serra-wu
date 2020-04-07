package it.polimi.ingsw.models.lobby;

/**
 * The "read-only" version of {@link Room}
 */
public interface RoomData {
    public int getRoomId();

    public String getRoomName();

    public UserData getHost();

    public int getNumberOfUsers();
}

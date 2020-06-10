package it.polimi.ingsw.models.lobby;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * This token is randomly generated and it uniquely identifies a user
 */
public final class UserToken implements Serializable {
    private final UUID uuid;

    public UserToken() {
        this.uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        UserToken userToken = (UserToken) o;
        return Objects.equals(this.uuid, userToken.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }
}

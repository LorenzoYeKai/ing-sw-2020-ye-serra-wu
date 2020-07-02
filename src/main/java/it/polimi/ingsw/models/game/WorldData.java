package it.polimi.ingsw.models.game;

import it.polimi.ingsw.InternalError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class WorldData implements Serializable, Iterable<Space> {
    private final List<Space> spaces;

    public WorldData(List<Space> spaces) {
        // the list created by List.copyOf is serializable if elements are
        // serializable, which is true
        this.spaces = List.copyOf(spaces);
    }

    public Space get(int x, int y) {
        if (!World.isInWorld(x, y)) {
            throw new InternalError("Out of range");
        }
        return this.spaces.get(y * World.SIZE + x);
    }

    public Space get(Vector2 position) {
        return this.get(position.getX(), position.getY());
    }

    public WorldData update(List<Space> newSpaces) {
        List<Space> newList = new ArrayList<>(this.spaces);
        for (Space space : newSpaces) {
            Vector2 position = space.getPosition();
            newList.set(position.getY() * World.SIZE + position.getX(), space);
        }
        return new WorldData(newList);
    }

    public Stream<Space> stream() {
        return this.spaces.stream();
    }

    @Override
    public Iterator<Space> iterator() {
        return this.spaces.iterator();
    }
}

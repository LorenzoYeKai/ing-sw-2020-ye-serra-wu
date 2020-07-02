package it.polimi.ingsw.models.game;

import it.polimi.ingsw.InternalError;

import java.io.Serializable;
import java.util.Objects;

public class Vector2 implements Serializable {
    private final int x;
    private final int y;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(x - other.x, y - other.y);
    }

    /**
     * Get the square magnitude of this vector.
     * It's possible to calculate the magnitude of vector by using square root.
     * But normally a square magnitude is already enough, and by using square
     * magnitude we don't run into problems such as decimal part rounding etc.
     * @return the square magnitude of this vector, i.e. dot(v, v)
     */
    public int squareMagnitude() {
        return x * x + y * y;
    }

    public boolean isNeighbor(Vector2 other) {
        return this.subtract(other).squareMagnitude() <= 2;
    }

    /**
     * Get the next {@link Vector2} after the target.
     * Used mainly with Minotaur's push power.
     * @param target the target coordinate, must be neighboring to current coordinate.
     * @return the coordinate after the target
     */
    public Vector2 getAfter(Vector2 target) {
        if(!this.isNeighbor(target)) {
            throw new InternalError("This should only be used with neighboring coordinates");
        }
        Vector2 direction = target.subtract(this);

        return target.add(direction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return this.getX() == vector2.getX() &&
                this.getY() == vector2.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getX(), this.getY());
    }

    @Override
    public String toString() {
        return "(" + x +", " + y + ")";
    }
}

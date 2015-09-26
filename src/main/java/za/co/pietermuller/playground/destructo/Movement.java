package za.co.pietermuller.playground.destructo;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Movement {

    private final double distance;
    private final Rotation rotation;

    /**
     * Creates a new Movement instance.
     *
     * @param distance distance to move, in meters
     * @param rotation rotation, anti-clockwise
     */
    public Movement(double distance, Rotation rotation) {
        this.distance = distance;
        this.rotation = rotation;
    }

    public double getDistance() {
        return distance;
    }

    public Rotation getRotation() {
        return rotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Movement movement = (Movement) o;
        return Objects.equal(distance, movement.distance) &&
               Objects.equal(rotation, movement.rotation);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(distance, rotation);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("distance", distance)
                .add("rotation", rotation)
                .toString();
    }
}

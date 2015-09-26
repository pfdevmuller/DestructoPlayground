package za.co.pietermuller.playground.destructo;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Rotation {

    private final double radians;

    public Rotation(double radians) {
        this.radians = radians;
    }

    public double radians() {
        return radians;
    }

    public double degrees() {
        return 180.0 * radians() / (Math.PI);
    }

    /**
     * Returns a new Rotation that is the sum of this and the given Rotation.
     *
     * @param other Rotation to combine with this one
     * @return A new Rotation instance that is the sum of this and the other
     */
    public Rotation add(Rotation other) {
        return Rotation.radians(this.radians() + other.radians());
    }

    /**
     * Returns a new Rotation that is equal to this one in orientation, but in the range [0, 360] degrees.
     *
     * @return new Rotation that is the normalized version of this instance
     */
    public Rotation normalize() {
        double newDegrees = this.degrees() % 360.0;
        if (newDegrees < 0) {
            newDegrees += 360.0;
        }
        return Rotation.degrees(newDegrees);
    }

    public static Rotation degrees(double value) {
        return new Rotation(value / 180.0 * Math.PI);
    }

    public static Rotation radians(double value) {
        return new Rotation(value);
    }

    public static Rotation noRotation() {
        return new Rotation(0.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Rotation rotation = (Rotation) o;
        return Objects.equal(radians, rotation.radians);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(radians);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("radians", radians)
                .toString();
    }
}

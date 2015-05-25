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

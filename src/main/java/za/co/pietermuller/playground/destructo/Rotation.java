package za.co.pietermuller.playground.destructo;

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
}

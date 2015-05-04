package za.co.pietermuller.playground.destructo;

public class Movement {

    private double deltaX;
    private double deltaXNoise;
    private double deltaY;
    private double deltaYNoise;
    private double rotation;
    private double rotationNoise;

    public Movement(double deltaX, double deltaXNoise, double deltaY, double deltaYNoise, double rotation, double rotationNoise) {
        this.deltaX = deltaX;
        this.deltaXNoise = deltaXNoise;
        this.deltaY = deltaY;
        this.deltaYNoise = deltaYNoise;
        this.rotation = rotation;
        this.rotationNoise = rotationNoise;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaXNoise() {
        return deltaXNoise;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public double getDeltaYNoise() {
        return deltaYNoise;
    }

    public double getRotation() {
        return rotation;
    }

    public double getRotationNoise() {
        return rotationNoise;
    }
}

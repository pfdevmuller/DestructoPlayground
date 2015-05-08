package za.co.pietermuller.playground.destructo.particlefilter;

import za.co.pietermuller.playground.destructo.Rotation;

import static za.co.pietermuller.playground.destructo.Rotation.radians;

public class Movement {

    private final double distance;
    private final double distanceNoise;
    private final Rotation rotation;
    private final Rotation rotationNoise;

    public Movement(double distance, Rotation rotation) {
        this(distance, 0, rotation, radians(0));
    }

    public Movement(double distance, double distanceNoise, Rotation rotation, Rotation rotationNoise) {
        this.distance = distance;
        this.distanceNoise = distanceNoise;
        this.rotation = rotation;
        this.rotationNoise = rotationNoise;
    }

    public double getDistance() {
        return distance;
    }

    public double getDistanceNoise() {
        return distanceNoise;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Rotation getRotationNoise() {
        return rotationNoise;
    }
}

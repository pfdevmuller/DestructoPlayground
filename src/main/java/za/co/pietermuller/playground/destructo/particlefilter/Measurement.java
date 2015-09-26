package za.co.pietermuller.playground.destructo.particlefilter;

public class Measurement {

    private final double distanceToWall;
    private final double distanceToWallNoise; // standard deviation

    public Measurement(double distanceToWall, double distanceToWallNoise) {
        this.distanceToWall = distanceToWall;
        this.distanceToWallNoise = distanceToWallNoise;
    }

    public double getDistanceToWall() {
        return distanceToWall;
    }

    public double getDistanceToWallNoise() {
        return distanceToWallNoise;
    }
}

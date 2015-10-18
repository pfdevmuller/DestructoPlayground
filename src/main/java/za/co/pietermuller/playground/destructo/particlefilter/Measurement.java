package za.co.pietermuller.playground.destructo.particlefilter;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Measurement that = (Measurement) o;
        return Objects.equal(distanceToWall, that.distanceToWall) &&
               Objects.equal(distanceToWallNoise, that.distanceToWallNoise);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(distanceToWall, distanceToWallNoise);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("distanceToWall", distanceToWall)
                .add("distanceToWallNoise", distanceToWallNoise)
                .toString();
    }
}

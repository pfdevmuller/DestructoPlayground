package za.co.pietermuller.playground.destructo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import math.geom2d.Vector2D;

import java.util.List;

import static za.co.pietermuller.playground.destructo.Rotation.radians;

public class AngleDistribution {

    private final Rotation mean;
    private final double confidence;

    public AngleDistribution(Rotation mean, double confidence) {
        this.mean = mean;
        this.confidence = confidence;
    }

    @JsonProperty("mean")
    public Rotation getMean() {
        return mean;
    }

    @JsonProperty("confidence")
    public double getConfidence() {
        return confidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AngleDistribution angleDistribution = (AngleDistribution) o;
        return Objects.equal(mean, angleDistribution.mean) &&
               Objects.equal(confidence, angleDistribution.confidence);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mean, confidence);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mean", mean)
                .add("confidence", confidence)
                .toString();
    }

    public static AngleDistribution fromValues(List<Rotation> rotations) {
        Vector2D acc = new Vector2D(0, 0);
        for (Rotation rotation : rotations) {
            acc = acc.plus(Vector2D.createPolar(1.0, rotation.radians()));
        }
        Vector2D resultingUnitVector = acc.times(1.0 / rotations.size());
        return new AngleDistribution(radians(resultingUnitVector.angle()), resultingUnitVector.norm());
    }

}

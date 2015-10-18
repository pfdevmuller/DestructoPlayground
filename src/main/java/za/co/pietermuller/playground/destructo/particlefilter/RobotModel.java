package za.co.pietermuller.playground.destructo.particlefilter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.slf4j.Logger;
import za.co.pietermuller.playground.destructo.Movement;
import za.co.pietermuller.playground.destructo.RobotDescription;
import za.co.pietermuller.playground.destructo.Rotation;
import za.co.pietermuller.playground.destructo.WorldModel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class RobotModel {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private final RobotDescription robotDescription;
    private Point2D position; // mm, cartesian
    private Rotation orientation; // anti-clockwise from positive x axis
    private final WorldModel worldModel;

    public RobotModel(RobotDescription robotDescription,
                      Point2D position,
                      Rotation orientation,
                      WorldModel worldModel) {
        this.robotDescription = checkNotNull(robotDescription, "robotDescription is null!");
        this.position = checkNotNull(position, "position is null!");
        this.orientation = checkNotNull(orientation, "orientation is null!");
        this.worldModel = checkNotNull(worldModel, "worldModel is null!");
    }

    @JsonProperty("orientation")
    public Rotation getOrientation() {
        return orientation;
    }

    @JsonProperty("position")
    public Point2D getPosition() {
        return position;
    }

    /**
     * Updates the robot's position and orientation.
     * Forward movement is performed first, then rotation.
     *
     * @param movement
     */
    public void move(Movement movement) {
        logger.debug("Moving robot by {}. Current position: {}. Current orientation: {}.",
                movement, getPosition(), getOrientation());

        // update position
        double x = position.x() + (Math.cos(orientation.radians()) * movement.getDistance());
        double y = position.y() + (Math.sin(orientation.radians()) * movement.getDistance());

        this.position = new Point2D(x, y);

        // update rotation:
        orientation = orientation.add(movement.getRotation()).normalize();

        logger.debug("Position after move: {}. Orientation after move: {}.",
                movement, getPosition(), getOrientation());
    }

    public boolean isInsideWorldBorder() {
        return worldModel.containsPoint(position);
    }

    /**
     * Returns an unnormalized probability of the given measurement being taken for this robot model.
     *
     * @param measurement
     * @return
     */
    public double getMeasurementProbability(Measurement measurement) {
        // TODO this assumes the sensor is looking along the same orientation as the robot

        double measurementProbability = 0;

        if (measurement.isInfinite()) {
            measurementProbability = 0;
        } else {

            LineSegment2D lineToWall = worldModel.getLineToNearestWall(position, orientation);
            double expectedMeasurement = lineToWall.length();

            double actualMeasurement =
                    measurement.getDistanceToWall() + robotDescription.getDistanceFromPositionToDistanceSensor();

            // How likely is the actual measurement, on a normal distribution based on
            // the expected measurement and noise?

            // If the noise is zero, the Gaussian fails, so we artificially limit it here:
            double noise = Math.max(
                    measurement.getDistanceToWallNoise(),
                    0.001);

            measurementProbability =
                    new Gaussian(expectedMeasurement, noise)
                            .value(actualMeasurement);
        }

        checkArgument(!Double.isNaN(measurementProbability), "Measurement Probability must be a real number.");

        logger.debug("Calculated measurement probability of {} for measurement {},"
                     + "given a robot at position {}, orientation {}.",
                measurementProbability, measurement, getPosition(), getOrientation());

        return measurementProbability;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orientation", orientation)
                .add("robotDescription", robotDescription)
                .add("position", position)
                .add("worldModel", worldModel)
                .toString();
    }

    static RobotModel copyOf(RobotModel other) {
        return new RobotModel(other.robotDescription, other.position, other.orientation, other.worldModel);
    }
}

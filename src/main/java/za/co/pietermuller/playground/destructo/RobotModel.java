package za.co.pietermuller.playground.destructo;

import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import org.apache.commons.math3.analysis.function.Gaussian;

public class RobotModel {

    private final RobotDescription robotDescription;
    private Point2D position; // mm, cartesian
    private double orientation; // radians, anti-clockwise from positive x axis

    public RobotModel(RobotDescription robotDescription, Point2D position, double orientation) {
        this.robotDescription = robotDescription;
        this.position = position;
        this.orientation = orientation;
    }

    public double getOrientation() {
        return orientation;
    }

    public Point2D getPosition() {
        return position;
    }

    public void move(Movement movement) {
        // TODO
    }

    /**
     * Returns an unnormalized probability of the given measurement being taken for this robot model.
     *
     * @param measurement
     * @param worldModel
     * @return
     */
    public double getMeasurementProbability(Measurement measurement, WorldModel worldModel) {
        // TODO this assumes the sensor is looking along the same orientation as the robot

        LineSegment2D lineToWall = worldModel.getLineToNearestWall(position, orientation);
        double expectedMeasurement = lineToWall.length();

        double actualMeasurement =
                measurement.getDistanceToWall() + robotDescription.getDistanceFromPositionToDistanceSensor();

        // How likely is the actual measurement, on a normal distribution based on
        // the expected measurement and noise?
        double measurementProbability =
                new Gaussian(expectedMeasurement, measurement.getDistanceToWallNoise())
                        .value(actualMeasurement);

        return measurementProbability;
    }
}

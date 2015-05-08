package za.co.pietermuller.playground.destructo.particlefilter;

import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import org.apache.commons.math3.analysis.function.Gaussian;
import za.co.pietermuller.playground.destructo.RobotDescription;
import za.co.pietermuller.playground.destructo.WorldModel;

import java.util.Random;

public class RobotModel {

    private final RobotDescription robotDescription;
    private Point2D position; // mm, cartesian
    private double orientation; // radians, anti-clockwise from positive x axis
    private final WorldModel worldModel;
    private final Random randomGenerator;

    public RobotModel(RobotDescription robotDescription,
                      Point2D position,
                      double orientation,
                      WorldModel worldModel,
                      Random randomGenerator) {
        this.robotDescription = robotDescription;
        this.position = position;
        this.orientation = orientation;
        this.worldModel = worldModel;
        this.randomGenerator = randomGenerator;
    }

    public double getOrientation() {
        return orientation;
    }

    public Point2D getPosition() {
        return position;
    }

    public void move(Movement movement) {
        // update rotation:
        double rotationNoise = randomGenerator.nextGaussian() * movement.getRotationNoise().radians();
        this.orientation += movement.getRotation().radians() + rotationNoise;

        // update position
        double distanceNoise = randomGenerator.nextGaussian() * movement.getDistanceNoise();
        double distanceToMove = movement.getDistance() + distanceNoise;

        double x = position.x() + (Math.cos(orientation) * distanceToMove);
        double y = position.y() + (Math.sin(orientation) * distanceToMove);

        this.position = new Point2D(x, y);

        if (!worldModel.containsPoint(position)) {
            moveBackToBorder();
        }
    }

    private void moveBackToBorder() {
        // TODO
    }

    /**
     * Returns an unnormalized probability of the given measurement being taken for this robot model.
     *
     * @param measurement
     * @return
     */
    public double getMeasurementProbability(Measurement measurement) {
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

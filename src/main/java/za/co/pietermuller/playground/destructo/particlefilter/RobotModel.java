package za.co.pietermuller.playground.destructo.particlefilter;

import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import org.apache.commons.math3.analysis.function.Gaussian;
import za.co.pietermuller.playground.destructo.Movement;
import za.co.pietermuller.playground.destructo.RobotDescription;
import za.co.pietermuller.playground.destructo.Rotation;
import za.co.pietermuller.playground.destructo.WorldModel;

import static com.google.common.base.Preconditions.checkNotNull;

public class RobotModel {

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

    public Rotation getOrientation() {
        return orientation;
    }

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
        // update position
        double x = position.x() + (Math.cos(orientation.radians()) * movement.getDistance());
        double y = position.y() + (Math.sin(orientation.radians()) * movement.getDistance());

        this.position = new Point2D(x, y);

        if (!worldModel.containsPoint(position)) {
            moveBackToBorder();
        }

        // update rotation:
        // TODO: Add() method for rotations
        orientation = Rotation.radians(orientation.radians() + movement.getRotation().radians());
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

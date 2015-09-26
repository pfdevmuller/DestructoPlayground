package za.co.pietermuller.playground.destructo.particlefilter;

import math.geom2d.Point2D;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.co.pietermuller.playground.destructo.Movement;
import za.co.pietermuller.playground.destructo.MovementNoiseModel;
import za.co.pietermuller.playground.destructo.RobotDescription;
import za.co.pietermuller.playground.destructo.Rotation;
import za.co.pietermuller.playground.destructo.WorldModel;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static za.co.pietermuller.playground.destructo.Rotation.degrees;

public class RobotModelTest {

    private RobotDescription testRobotDescription = new RobotDescription() {
        public double getDistanceFromPositionToDistanceSensor() {
            return 10;
        }

        public double getDriverWheelDiameter() {
            throw new UnsupportedOperationException("getDriverWheelDiameter not implemented!");
        }

        public double getAxleLength() {
            throw new UnsupportedOperationException("getAxleLength not implemented!");
        }

        public MovementNoiseModel getDistanceMovementNoiseModel() {
            throw new UnsupportedOperationException("getDistanceMovementNoiseModel not implemented!");
        }

        public MovementNoiseModel getRotationMovementNoiseModel() {
            throw new UnsupportedOperationException("getRotationMovementNoiseModel not implemented!");
        }
    };

    @Mock
    Random mockRandom;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetMeasurementProbability() throws Exception {
        // given
        WorldModel worldModel = getSimpleSquareWorld();
        Point2D position = new Point2D(50, 75);
        Rotation orientation = degrees(90); // Due north
        double measurementDistance = 15;
        double measurementNoise = 2;

        RobotModel robotModel =
                new RobotModel(testRobotDescription, position, orientation, worldModel);

        Measurement measurement = new Measurement(measurementDistance, measurementNoise);

        // expectedProbability = new Gaussian(25, 2).value(15 + 10) = 0.199471

        // when
        double probability = robotModel.getMeasurementProbability(measurement);

        // then
        assertThat(probability, is(closeTo(0.199471, 0.000001)));
    }

    @Test
    public void testMovementUpdatesOrientation() throws Exception {
        // given
        WorldModel worldModel = getSimpleSquareWorld();
        Point2D position = new Point2D(50, 50);
        Rotation orientation = degrees(90); // Due north

        RobotModel robotModel =
                new RobotModel(testRobotDescription, position, orientation, worldModel);

        Movement movement = new Movement(10, degrees(10));

        // when
        robotModel.move(movement);

        // then
        assertThat(robotModel.getOrientation(), is(equalTo(degrees(90 + 10))));
    }

    @Test
    public void testMovementUpdatesPosition() throws Exception {
        // given
        WorldModel worldModel = getSimpleSquareWorld();
        Point2D position = new Point2D(100, 100);
        Rotation orientation = degrees(60);

        RobotModel robotModel =
                new RobotModel(testRobotDescription, position, orientation, worldModel);

        Movement movement = new Movement(2, degrees(10));

        // when
        robotModel.move(movement);

        // then
        Point2D expectedPosition = new Point2D(
                100 + 1,
                100 + Math.sqrt(3));

        assertThat(robotModel.getPosition(), is(equalTo(expectedPosition)));
    }

    private WorldModel getSimpleSquareWorld() {
        return WorldModel.builder()
                .withBoundaryPoint(new Point2D(0, 0))
                .withBoundaryPoint(new Point2D(100, 0))
                .withBoundaryPoint(new Point2D(100, 100))
                .withBoundaryPoint(new Point2D(0, 100))
                .build();
    }
}

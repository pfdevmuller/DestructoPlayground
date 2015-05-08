package za.co.pietermuller.playground.destructo.particlefilter;

import math.geom2d.Point2D;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.co.pietermuller.playground.destructo.RobotDescription;
import za.co.pietermuller.playground.destructo.WorldModel;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

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
        double orientation = 0.5 * Math.PI; // Due north
        double measurementDistance = 15;
        double measurementNoise = 2;

        RobotModel robotModel =
                new RobotModel(testRobotDescription, position, orientation, worldModel, mockRandom);

        Measurement measurement = new Measurement(measurementDistance, measurementNoise);

        // expectedProbability = new Gaussian(25, 2).value(15 + 10) = 0.199471

        // when
        double probability = robotModel.getMeasurementProbability(measurement);

        // then
        assertThat(probability, is(closeTo(0.199471, 0.000001)));
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

package za.co.pietermuller.playground.destructo;

import math.geom2d.Point2D;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class RobotModelTest {

    private RobotDescription testRobotDescription = new RobotDescription() {
        public double getDistanceFromPositionToDistanceSensor() {
            return 10;
        }
    };


    @Test
    public void testGetMeasurementProbability() throws Exception {
        // given
        WorldModel worldModel = getSimpleSquareWorld();
        Point2D position = new Point2D(50, 75);
        double orientation = 0.5 * Math.PI; // Due north
        double measurementDistance = 15;
        double measurementNoise = 2;

        RobotModel robotModel = new RobotModel(testRobotDescription, position, orientation);

        Measurement measurement = new Measurement(measurementDistance, measurementNoise);

        // expectedProbability = new Gaussian(25, 2).value(15 + 10) = 0.199471

        // when
        double probability = robotModel.getMeasurementProbability(measurement, worldModel);

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

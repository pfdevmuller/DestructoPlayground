package za.co.pietermuller.playground.destructo.particlefilter;

import math.geom2d.Point2D;
import org.junit.Test;
import za.co.pietermuller.playground.destructo.DestructoDescription;
import za.co.pietermuller.playground.destructo.Movement;
import za.co.pietermuller.playground.destructo.WorldModel;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static za.co.pietermuller.playground.destructo.Rotation.degrees;
import static za.co.pietermuller.playground.destructo.Rotation.noRotation;

public class ParticleFilterIntegrationTest {

    DestructoDescription destructoDescription = new DestructoDescription();

    WorldModel worldModel = getFlatLShapedWorld();

    Random random = new Random();

    RandomParticleSource randomParticleSource =
            new RandomParticleSource(destructoDescription, worldModel, random, 1000);

    SamplingStrategy<RobotModel> samplingStrategy = new SimpleRandomSamplingStrategy<RobotModel>(random);

    NoisyMovementFactory noisyMovementFactory = new NoisyMovementFactory(destructoDescription, random);

    @Test
    public void testFilterConverges() throws Exception {
        // given
        RobotModel actualRobot = new RobotModel(
                destructoDescription, new Point2D(90, 10), degrees(0), worldModel);

        ParticleFilter particleFilter =
                new ParticleFilter(randomParticleSource, samplingStrategy, noisyMovementFactory);

        Movement[] movements = new Movement[]{
                new Movement(0, degrees(-45)),
                new Movement(0, degrees(-45)),
                new Movement(0, degrees(-45)),
                new Movement(0, degrees(-45)),
                new Movement(40, noRotation()),
                new Movement(40, noRotation()),
                new Movement(0, degrees(270)), // Facing north
                new Movement(25, noRotation()), // Facing north

                new Movement(0, degrees(45)),
                new Movement(0, degrees(45)), // Facing west
                new Movement(0, degrees(45)),
                new Movement(0, degrees(45)), // Facing south
                new Movement(25, noRotation()), // Facing south
                new Movement(0, degrees(45)),
                new Movement(0, degrees(45)), // Facing east
        };

        Measurement[] measurements = new Measurement[]{
                new Measurement(14, 2), // corner at (100, 0)
                new Measurement(11, 2), // wall at (90, 0)
                new Measurement(15, 2), // wall at (80, 0)
                new Measurement(88, 10), // wall at (0, 10)
                new Measurement(52, 5),  // wall at (0, 10)
                new Measurement(10, 2),  // wall at (0, 10)
                new Measurement(30, 3), // wall at (10, 40)
                new Measurement(5, 2), // wall at (10, 40)

                new Measurement(14, 2), // corner at (0, 40)
                new Measurement(10, 2), // wall at (0, 30)
                new Measurement(14, 2), // wall at (0, 20)
                new Measurement(35, 3), // wall at (10, 0)
                new Measurement(10, 2), // wall at (10, 0)
                new Measurement(14, 2), // wall at (20, 0)
                new Measurement(90, 2), // wall at (100, 10)

        };

        assertThat("movements and measurements must be equal in number",
                movements.length, is(equalTo(measurements.length)));

        // when
        for (int i = 0; i < movements.length; i++) {
            actualRobot.move(movements[i]);
            System.out.println("Robot moved to " + actualRobot.getPosition());
            particleFilter.movementUpdate(movements[i]);
            particleFilter.measurementUpdate(measurements[i]);
            System.out.println(
                    String.format("Average X: %.2f, Average Y: %.2f, Average Orientation: %.2f",
                            particleFilter.getDistributionAlongXAxis().getMean(),
                            particleFilter.getDistributionAlongYAxis().getMean(),
                            particleFilter.getDistributionOfOrientations().getMean().degrees()));
            //printDistributions(particleFilter);
        }

        // then
        double xError = Math.abs(particleFilter.getDistributionAlongXAxis().getMean() - actualRobot.getPosition().x());
        double yError = Math.abs(particleFilter.getDistributionAlongYAxis().getMean() - actualRobot.getPosition().y());
        double orientationError =
                Math.abs(
                        particleFilter.getDistributionOfOrientations().getMean().degrees()
                        - actualRobot.getOrientation().degrees());
        orientationError = ((orientationError + 180) % 360) - 180; // Normalize

        assertThat("Expected small error in x position estimate", xError, lessThan(3.0));
        assertThat("Expected small error in y position estimate", yError, lessThan(3.0));
        assertThat("Expected small error in orientation estimate", orientationError, lessThan(5.0));
    }

    private WorldModel getFlatLShapedWorld() {
        return WorldModel.builder()
                .withBoundaryPoint(new Point2D(0, 0))
                .withBoundaryPoint(new Point2D(100, 0))
                .withBoundaryPoint(new Point2D(100, 20))
                .withBoundaryPoint(new Point2D(20, 20))
                .withBoundaryPoint(new Point2D(20, 40))
                .withBoundaryPoint(new Point2D(0, 40))
                .build();
    }
}

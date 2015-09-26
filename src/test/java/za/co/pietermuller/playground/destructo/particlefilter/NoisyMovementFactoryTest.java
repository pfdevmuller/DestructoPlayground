package za.co.pietermuller.playground.destructo.particlefilter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.co.pietermuller.playground.destructo.Movement;
import za.co.pietermuller.playground.destructo.MovementNoiseModel;
import za.co.pietermuller.playground.destructo.RobotDescription;
import za.co.pietermuller.playground.destructo.Rotation;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class NoisyMovementFactoryTest {

    @Mock
    RobotDescription robotDescription;

    @Mock
    MovementNoiseModel distanceNoiseModel;

    @Mock
    MovementNoiseModel rotationNoiseModel;

    @Mock
    Random randomGenerator;

    // Values are arbitrary (Doubles cannot be mocked)
    double rand1 = 0.13;
    double rand2 = 0.17;
    double rand3 = 0.19;
    double rand4 = 0.23;

    double unityDistanceNoiseDueToDistanceMovement = 0.3;
    double unityRotationNoiseDueToDistanceMovement = 0.5;
    double unityDistanceNoiseDueToRotationMovement = 0.7;
    double unityRotationNoiseDueToRotationMovement = 1.1;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(robotDescription.getDistanceMovementNoiseModel()).thenReturn(distanceNoiseModel);
        when(robotDescription.getRotationMovementNoiseModel()).thenReturn(rotationNoiseModel);

        when(randomGenerator.nextGaussian())
                .thenReturn(rand1)
                .thenReturn(rand2)
                .thenReturn(rand3)
                .thenReturn(rand4);

        when(distanceNoiseModel.scaleDistanceGuassian(rand1))
                .thenReturn(unityDistanceNoiseDueToDistanceMovement);
        when(distanceNoiseModel.scaleRotationGaussian(rand2))
                .thenReturn(unityRotationNoiseDueToDistanceMovement);
        when(rotationNoiseModel.scaleDistanceGuassian(rand3))
                .thenReturn(unityDistanceNoiseDueToRotationMovement);
        when(rotationNoiseModel.scaleRotationGaussian(rand4))
                .thenReturn(unityRotationNoiseDueToRotationMovement);
    }

    @Test
    public void testCreateNoisyMovementForwardOnly() throws Exception {
        // given
        NoisyMovementFactory noisyMovementFactory =
                new NoisyMovementFactory(robotDescription, randomGenerator);

        Movement noiselessMovement = new Movement(100, Rotation.noRotation());

        // when
        Movement noisyMovement = noisyMovementFactory.createNoisyMovement(noiselessMovement);

        // then
        double expectedDistance =
                noiselessMovement.getDistance() * (1 + unityDistanceNoiseDueToDistanceMovement);
        double expectedRotation =
                noiselessMovement.getDistance() * (0 + unityRotationNoiseDueToDistanceMovement);
        Movement expectedMovement = new Movement(expectedDistance, Rotation.radians(expectedRotation));

        assertThat(noisyMovement, is(equalTo(expectedMovement)));
    }

    @Test
    public void testCreateNoisyMovementRotationOnly() throws Exception {
        // given
        NoisyMovementFactory noisyMovementFactory =
                new NoisyMovementFactory(robotDescription, randomGenerator);

        Movement noiselessMovement = new Movement(0, Rotation.radians(100));

        // when
        Movement noisyMovement = noisyMovementFactory.createNoisyMovement(noiselessMovement);

        // then
        double expectedDistance =
                noiselessMovement.getRotation().radians() * (0 + unityDistanceNoiseDueToRotationMovement);
        double expectedRotation =
                noiselessMovement.getRotation().radians() * (1 + unityRotationNoiseDueToRotationMovement);
        Movement expectedMovement = new Movement(expectedDistance, Rotation.radians(expectedRotation));

        assertThat(noisyMovement, is(equalTo(expectedMovement)));
    }

    @Test
    public void testCreateNoisyMovementForwardAndRotation() throws Exception {
        // given
        NoisyMovementFactory noisyMovementFactory =
                new NoisyMovementFactory(robotDescription, randomGenerator);

        Movement noiselessMovement = new Movement(100, Rotation.radians(50));

        // when
        Movement noisyMovement = noisyMovementFactory.createNoisyMovement(noiselessMovement);

        // then
        double expectedDistance =
                noiselessMovement.getDistance() * (1 + unityDistanceNoiseDueToDistanceMovement)
                + noiselessMovement.getRotation().radians() * (0 + unityDistanceNoiseDueToRotationMovement);

        double expectedRotation =
                noiselessMovement.getDistance() * (0 + unityRotationNoiseDueToDistanceMovement)
                + noiselessMovement.getRotation().radians() * (1 + unityRotationNoiseDueToRotationMovement);

        Movement expectedMovement = new Movement(expectedDistance, Rotation.radians(expectedRotation));

        assertThat(noisyMovement, is(equalTo(expectedMovement)));
    }
}

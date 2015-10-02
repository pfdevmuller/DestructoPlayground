package za.co.pietermuller.playground.destructo.particlefilter;

import com.google.common.collect.ImmutableList;
import math.geom2d.Point2D;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.co.pietermuller.playground.destructo.AngleDistribution;
import za.co.pietermuller.playground.destructo.Gaussian;
import za.co.pietermuller.playground.destructo.Movement;
import za.co.pietermuller.playground.destructo.RobotDescription;
import za.co.pietermuller.playground.destructo.WorldModel;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static za.co.pietermuller.playground.destructo.Rotation.degrees;
import static za.co.pietermuller.playground.destructo.Rotation.radians;

public class ParticleFilterTest {

    @Mock
    WorldModel mockWorldModel;

    @Mock
    RandomParticleSource mockRandomParticleSource;

    @Mock
    SamplingStrategy mockSamplingStrategy;

    @Mock
    NoisyMovementFactory mockNoisyMovementFactory;

    @Mock
    RobotDescription mockRobotDescription;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMovementUpdate() throws Exception {
        // given
        RobotModel model1 = mock(RobotModel.class);
        RobotModel model2 = mock(RobotModel.class);
        RobotModel model3 = mock(RobotModel.class);

        when(mockRandomParticleSource.getRandomParticles())
                .thenReturn(ImmutableList.of(model1, model2, model3));

        ParticleFilter particleFilter = new ParticleFilter(
                mockRandomParticleSource,
                mockSamplingStrategy,
                mockNoisyMovementFactory);

        Movement noiselessMovement = mock(Movement.class);
        Movement noisyMovement1 = mock(Movement.class);
        Movement noisyMovement2 = mock(Movement.class);
        Movement noisyMovement3 = mock(Movement.class);
        when(mockNoisyMovementFactory.createNoisyMovement(noiselessMovement))
                .thenReturn(noisyMovement1)
                .thenReturn(noisyMovement2)
                .thenReturn(noisyMovement3);

        // when
        particleFilter.movementUpdate(noiselessMovement);

        // then
        verify(model1, times(1)).move(noisyMovement1);
        verify(model2, times(1)).move(noisyMovement2);
        verify(model3, times(1)).move(noisyMovement3);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMeasurementUpdate() throws Exception {
        // given
        RobotModel model1 = mock(RobotModel.class);
        RobotModel model2 = mock(RobotModel.class);
        RobotModel model3 = mock(RobotModel.class);

        when(mockRandomParticleSource.getRandomParticles())
                .thenReturn(ImmutableList.of(model1, model2, model3));

        ParticleFilter particleFilter = new ParticleFilter(
                mockRandomParticleSource,
                mockSamplingStrategy,
                mockNoisyMovementFactory);

        Measurement measurement = new Measurement(10, 0);

        // when
        particleFilter.measurementUpdate(measurement);

        // then
        verify(model1, times(1)).getMeasurementProbability(measurement);
        verify(model2, times(1)).getMeasurementProbability(measurement);
        verify(model3, times(1)).getMeasurementProbability(measurement);
        verify(mockSamplingStrategy, times(1)).sampleFrom(
                (List<WeightedObject<RobotModel>>) Matchers.argThat(
                        contains(
                                weightedObjectWithRobotModel(model1),
                                weightedObjectWithRobotModel(model2),
                                weightedObjectWithRobotModel(model3))));
    }

    @Test
    public void testGetDistributions() throws Exception {
        // given
        double[] xValues = new double[]{
                4.867785633011822, 9.356311517808276, 4.908501318107904, 1.5619486939821103, 8.506688502573386
        };

        double[] yValues = new double[]{
                38.05504312454009, 36.21562929573998, 33.756868099291346, 4.079361477149912, 8.9930272335837
        };

        double[] orientations = new double[]{
                7.47703203958018, 17.070376094462187, 12.924520331791127, 6.226623883197327, 17.235718043457304
        };

        List<RobotModel> particles = new ArrayList<RobotModel>();
        for (int i = 0; i < 5; i++) {
            particles.add(new RobotModel(
                    mockRobotDescription, new Point2D(xValues[i], yValues[i]), radians(orientations[i]), mockWorldModel));
        }

        when(mockRandomParticleSource.getRandomParticles())
                .thenReturn(particles);

        ParticleFilter particleFilter =
                new ParticleFilter(mockRandomParticleSource, mockSamplingStrategy, mockNoisyMovementFactory);

        // when
        Gaussian xDistribution = particleFilter.getDistributionAlongXAxis();
        Gaussian yDistribution = particleFilter.getDistributionAlongYAxis();
        AngleDistribution orientationDistribution = particleFilter.getDistributionOfOrientations();

        // then
        assertThat(xDistribution.getMean(), is(closeTo(5.840, 0.001)));
        assertThat(xDistribution.getSigma(), is(closeTo(3.146, 0.001)));
        assertThat(yDistribution.getMean(), is(closeTo(24.219, 0.001)));
        assertThat(yDistribution.getSigma(), is(closeTo(16.307, 0.001)));
        assertThat(orientationDistribution.getMean().radians(), is(closeTo(5.931386434064327, 0.001)));
        assertThat(orientationDistribution.getConfidence(), is(closeTo(0.4374151485439101, 0.001)));
    }

    @Test
    public void testGetStatus() throws Exception {
        // given
        RandomParticleSource mockParticleSource = mock(RandomParticleSource.class);
        when(mockParticleSource.getRandomParticles()).thenReturn(
                ImmutableList.of(
                        new RobotModel(mockRobotDescription, new Point2D(1, 2), degrees(45), mockWorldModel),
                        new RobotModel(mockRobotDescription, new Point2D(3, 4), degrees(135), mockWorldModel))
        );

        ParticleFilter particleFilter =
                new ParticleFilter(mockParticleSource, mockSamplingStrategy, mockNoisyMovementFactory);

        // when
        String status = particleFilter.getStatus();

        // then
        // We only really check that it doesn't blow up, and returns something resembling info about a particle filter

        assertThat(status, containsString("distributionAlongXAxis"));
        assertThat(status, containsString("distributionAlongYAxis"));
        assertThat(status, containsString("distributionOfOrientations"));
        assertThat(status, containsString("particles"));
    }

    private Matcher<WeightedObject<RobotModel>> weightedObjectWithRobotModel(RobotModel robotModel) {
        return weightedObjectWithRobotModel(equalTo(robotModel));
    }

    private Matcher<WeightedObject<RobotModel>> weightedObjectWithRobotModel(Matcher<RobotModel> robotModelMatcher) {
        return new FeatureMatcher<WeightedObject<RobotModel>, RobotModel>(robotModelMatcher, "robot model", "object") {
            @Override
            protected RobotModel featureValueOf(WeightedObject<RobotModel> robotModelWeightedObject) {
                return robotModelWeightedObject.getObject();
            }
        };
    }
}

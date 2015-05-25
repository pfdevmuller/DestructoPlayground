package za.co.pietermuller.playground.destructo.particlefilter;

import com.google.common.collect.ImmutableList;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.co.pietermuller.playground.destructo.Movement;
import za.co.pietermuller.playground.destructo.WorldModel;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParticleFilterTest {

    @Mock
    WorldModel mockWorldModel;

    @Mock
    RandomParticleSource mockRandomParticleSource;

    @Mock
    SamplingStrategy mockSamplingStrategy;

    @Mock
    NoisyMovementFactory mockNoisyMovementFactory;

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

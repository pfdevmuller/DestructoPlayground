package za.co.pietermuller.playground.destructo.particlefilter;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleRandomSamplingStrategyTest {

    @Test
    public void testSampleFrom() throws Exception {
        // given
        WeightedObject<String> weightedObject1 = new WeightedObject<String>("object1", 1);
        WeightedObject<String> weightedObject2 = new WeightedObject<String>("object2", 5);
        WeightedObject<String> weightedObject3 = new WeightedObject<String>("object3", 2);
        WeightedObject<String> weightedObject4 = new WeightedObject<String>("object4", 2);

        // Total weight is 10

        Random randomMock = mock(Random.class);
        when(randomMock.nextDouble()).thenReturn(0.2, 0.2, 0.8, 0.0);

        List<String> expectedSamples = ImmutableList.of("object2", "object2", "object4", "object1");

        SimpleRandomSamplingStrategy<String> samplingStrategy = new SimpleRandomSamplingStrategy<String>(randomMock);

        // when
        List<String> actualSamples = samplingStrategy.sampleFrom(ImmutableList.of(
                weightedObject1, weightedObject2, weightedObject3, weightedObject4));

        // then
        assertThat(expectedSamples, is(equalTo(actualSamples)));
    }

    @Test
    public void testSampleFromWithTotalWeightZeroReturnsInput() throws Exception {
        // given
        WeightedObject<String> weightedObject1 = new WeightedObject<String>("object1", 0);
        WeightedObject<String> weightedObject2 = new WeightedObject<String>("object2", 0);
        WeightedObject<String> weightedObject3 = new WeightedObject<String>("object3", 0);
        WeightedObject<String> weightedObject4 = new WeightedObject<String>("object4", 0);

        // Total weight is 0

        Random randomMock = mock(Random.class);

        List<String> expectedSamples = ImmutableList.of("object1", "object2", "object3", "object4");

        SimpleRandomSamplingStrategy<String> samplingStrategy = new SimpleRandomSamplingStrategy<String>(randomMock);

        // when
        List<String> actualSamples = samplingStrategy.sampleFrom(ImmutableList.of(
                weightedObject1, weightedObject2, weightedObject3, weightedObject4));

        // then
        assertThat(expectedSamples, is(equalTo(actualSamples)));
    }
}

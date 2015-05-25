package za.co.pietermuller.playground.destructo;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class MovementNoiseModelTest {

    @Test
    public void testScaleDistanceGuassian() throws Exception {
        // given
        double mean = 100;
        double sigma = 50;

        double scalingRandom = -0.5;

        MovementNoiseModel movementNoiseModel = new MovementNoiseModel(100, 50, 0, 0);

        // when
        double noise = movementNoiseModel.scaleDistanceGuassian(scalingRandom);

        // then
        double expectation = scalingRandom * sigma + mean;
        assertThat(noise, is(equalTo(expectation)));
    }

    @Test
    public void testScaleRotationGaussian() throws Exception {
        // given
        double mean = 100;
        double sigma = 50;

        double scalingRandom = -0.5;

        MovementNoiseModel movementNoiseModel = new MovementNoiseModel(0, 0, 100, 50);

        // when
        double noise = movementNoiseModel.scaleRotationGaussian(scalingRandom);

        // then
        double expectation = scalingRandom * sigma + mean;
        assertThat(noise, is(equalTo(expectation)));
    }
}

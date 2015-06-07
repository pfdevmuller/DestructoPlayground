package za.co.pietermuller.playground.destructo;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static za.co.pietermuller.playground.destructo.Rotation.degrees;

public class AngleDistributionTest {

    @Test
    public void testFromValuesWithSingleAngle() throws Exception {
        // when
        AngleDistribution angleDistribution = AngleDistribution.fromValues(
                ImmutableList.of(
                        degrees(90)));

        // then
        assertThat(angleDistribution, is(equalTo(new AngleDistribution(degrees(90), 1.0))));
    }

    @Test
    public void testFromValuesWithMultipleAlignedAngles() throws Exception {
        AngleDistribution angleDistribution = AngleDistribution.fromValues(
                ImmutableList.of(
                        degrees(270),
                        degrees(270),
                        degrees(270)));

        // then
        assertThat(angleDistribution, is(equalTo(new AngleDistribution(degrees(270), 1.0))));
    }

    @Test
    public void testFromValuesWithMultipleNonAlignedAngles() throws Exception {
        AngleDistribution angleDistribution = AngleDistribution.fromValues(
                ImmutableList.of(
                        degrees(90),
                        degrees(270),
                        degrees(180)));

        // then
        assertThat(angleDistribution.getMean().degrees(), is(closeTo(180, 0.001)));
        assertThat(angleDistribution.getConfidence(), is(closeTo(1.0 / 3.0, 0.001)));
    }

    @Test
    public void testFromValuesWithOpposingAngles() throws Exception {
        AngleDistribution angleDistribution = AngleDistribution.fromValues(
                ImmutableList.of(
                        degrees(45),
                        degrees(225)));

        // then
        // The mean is undefined, and we don't care
        assertThat(angleDistribution.getConfidence(), is(closeTo(0.0, 0.001)));
    }
}

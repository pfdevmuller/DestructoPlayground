package za.co.pietermuller.playground.destructo;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class StatsUtilitiesTest {

    @Test
    public void testGaussian() throws Exception {
        // given
        double mu = 5.0;
        double sigma = 2.0;
        double x = 4.0;

        // then
        assertThat(StatsUtilities.gaussian(mu, sigma, x), is(closeTo(0.176, 0.001)));
    }
}

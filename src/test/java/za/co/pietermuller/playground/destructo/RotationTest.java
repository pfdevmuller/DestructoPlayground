package za.co.pietermuller.playground.destructo;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static za.co.pietermuller.playground.destructo.Rotation.radians;

public class RotationTest {

    @Test
    public void testRadians() throws Exception {
        // given
        Rotation test = radians(5.0);

        // then
        assertThat(test.radians(), is(equalTo(5.0)));
    }

    @Test
    public void testDegrees() throws Exception {
        // given
        Rotation test = radians(Math.PI);

        // then
        assertThat(test.degrees(), is(closeTo(180.0, 0.001)));
    }
}

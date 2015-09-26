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

    @Test
    public void testAddition() throws Exception {
        assertThat(Rotation.degrees(5.0).add(Rotation.degrees(10.0)), is(equalTo(Rotation.degrees(15.0))));
        assertThat(Rotation.degrees(-5.0).add(Rotation.degrees(-10.0)), is(equalTo(Rotation.degrees(-15.0))));
        assertThat(Rotation.degrees(5.0).add(Rotation.degrees(-10.0)), is(equalTo(Rotation.degrees(-5.0))));
        assertThat(Rotation.degrees(270.0).add(Rotation.degrees(180.0)), is(equalTo(Rotation.degrees(450.0))));
    }

    @Test
    public void testNormalize() throws Exception {
        assertThat(Rotation.degrees(15.0).normalize(), is(equalTo(Rotation.degrees(15.0))));
        assertThat(Rotation.degrees(-15.0).normalize(), is(equalTo(Rotation.degrees(345.0))));
        assertThat(Rotation.degrees(450.0).normalize(), is(equalTo(Rotation.degrees(90.0))));
        assertThat(Rotation.degrees(-450.0).normalize(), is(equalTo(Rotation.degrees(270.0))));
    }
}

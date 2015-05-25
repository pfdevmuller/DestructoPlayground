package za.co.pietermuller.playground.destructo.particlefilter;

import math.geom2d.Point2D;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sun.plugin.dom.exception.InvalidStateException;
import za.co.pietermuller.playground.destructo.RobotDescription;
import za.co.pietermuller.playground.destructo.WorldModel;

import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomParticleSourceTest {

    @Mock
    RobotDescription robotDescription;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testParticlesAreInsideWorldBoundary() throws Exception {
        // given
        WorldModel worldModel = getSimpleLWorld(); // 100 x 100, with 50x50 in top right missing
        Random mockRandomGenerator = mock(Random.class);
        RandomParticleSource randomParticleSource =
                new RandomParticleSource(robotDescription, worldModel, mockRandomGenerator, 2);

        when(mockRandomGenerator.nextDouble())
                .thenReturn(0.8) // particle 1 x
                .thenReturn(0.1) // particle 1 y
                .thenReturn(0.5) // particle 1 angle
                .thenReturn(0.8) // rejected particle x
                .thenReturn(0.7) // rejected particle y
                .thenReturn(0.2) // particle 2 x
                .thenReturn(0.8) // particle 2 y
                .thenReturn(0.5) // particle 2 angle
                .thenThrow(new InvalidStateException("Should not be called this many times."));

        // when
        List<RobotModel> particles = randomParticleSource.getRandomParticles();

        // then
        assertThat(particles, contains(
                robotModelAt(equalTo(new Point2D(80, 10))),
                robotModelAt(equalTo(new Point2D(20, 80)))));
    }

    private Matcher<RobotModel> robotModelAt(Matcher<Point2D> positionMatcher) {
        return new FeatureMatcher<RobotModel, Point2D>(positionMatcher, "Robot Model Position", "position") {
            @Override
            protected Point2D featureValueOf(RobotModel robotModel) {
                return robotModel.getPosition();
            }
        };
    }

    private WorldModel getSimpleLWorld() {
        return WorldModel.builder()
                .withBoundaryPoint(new Point2D(0, 0))
                .withBoundaryPoint(new Point2D(100, 0))
                .withBoundaryPoint(new Point2D(100, 50))
                .withBoundaryPoint(new Point2D(50, 50))
                .withBoundaryPoint(new Point2D(50, 100))
                .withBoundaryPoint(new Point2D(0, 100))
                .build();
    }
}

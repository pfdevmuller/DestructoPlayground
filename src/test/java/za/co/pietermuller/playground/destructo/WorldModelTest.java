package za.co.pietermuller.playground.destructo;

import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static za.co.pietermuller.playground.destructo.Rotation.degrees;
import static za.co.pietermuller.playground.destructo.Rotation.noRotation;

public class WorldModelTest {

    @Test
    public void testGetLineToNearestWallWithSingleIntersection() throws Exception {
        // given
        WorldModel worldModel = getSimpleSquareWorld();

        Point2D sourcePoint = new Point2D(10, 10);

        // then
        assertThat(
                worldModel.getLineToNearestWall(sourcePoint, noRotation()),
                is(almostEqualTo(new LineSegment2D(10, 10, 100, 10))));
        assertThat(
                worldModel.getLineToNearestWall(sourcePoint, degrees(90)),
                is(almostEqualTo(new LineSegment2D(10, 10, 10, 100))));
        assertThat(
                worldModel.getLineToNearestWall(sourcePoint, degrees(180)),
                is(almostEqualTo(new LineSegment2D(10, 10, 0, 10))));
        assertThat(
                worldModel.getLineToNearestWall(sourcePoint, degrees(270)),
                is(almostEqualTo(new LineSegment2D(10, 10, 10, 0))));
        assertThat(
                worldModel.getLineToNearestWall(sourcePoint, degrees(45)),
                is(almostEqualTo(new LineSegment2D(10, 10, 100, 100))));
    }

    @Test
    public void testGetLineToNearestWallWithMultipleIntersections() throws Exception {
        // given
        WorldModel worldModel = getHorseShoeWorld();

        Point2D sourcePoint = new Point2D(10, 75);

        // then
        assertThat(
                worldModel.getLineToNearestWall(sourcePoint, noRotation()),
                is(almostEqualTo(new LineSegment2D(10, 75, 25, 75))));
    }

    @Test
    public void testContainsPointSimpleBox() throws Exception {
        // given
        WorldModel worldModel = getSimpleSquareWorld();

        Point2D pointInside = new Point2D(50, 50);
        Point2D pointOutside = new Point2D(-50, 50);
        Point2D pointOnLine = new Point2D(0, 0);

        // then
        assertThat("point should be inside", worldModel.containsPoint(pointInside), is(true));
        assertThat("point should be outside", worldModel.containsPoint(pointOutside), is(false));
        assertThat("point on line should be outside", worldModel.containsPoint(pointOnLine), is(false));
    }

    @Test
    public void testContainsPointHorseShoe() throws Exception {
        // given
        WorldModel worldModel = getHorseShoeWorld();

        Point2D pointInside = new Point2D(50, 25);
        Point2D pointOutside = new Point2D(50, 75);
        Point2D pointOnLine = new Point2D(50, 0);

        // then
        assertThat("point should be inside", worldModel.containsPoint(pointInside), is(true));
        assertThat("point should be outside", worldModel.containsPoint(pointOutside), is(false));
        assertThat("point on line should be outside", worldModel.containsPoint(pointOnLine), is(false));
    }

    @Test
    public void testGetBoundingBox() throws Exception {
        // given
        WorldModel worldModel = WorldModel.builder()
                .withBoundaryPoint(new Point2D(-40, 20))
                .withBoundaryPoint(new Point2D(0, 60))
                .withBoundaryPoint(new Point2D(80, -30))
                .build();

        // when
        Box2D boundingBox = worldModel.getBoundingBox();

        // then
        assertThat(boundingBox.getMaxX(), is(equalTo(80.0)));
        assertThat(boundingBox.getMinX(), is(equalTo(-40.0)));
        assertThat(boundingBox.getMaxY(), is(equalTo(60.0)));
        assertThat(boundingBox.getMinY(), is(equalTo(-30.0)));
    }

    private WorldModel getSimpleSquareWorld() {
        return WorldModel.builder()
                .withBoundaryPoint(new Point2D(0, 0))
                .withBoundaryPoint(new Point2D(100, 0))
                .withBoundaryPoint(new Point2D(100, 100))
                .withBoundaryPoint(new Point2D(0, 100))
                .build();
    }

    private WorldModel getHorseShoeWorld() {
        return WorldModel.builder()
                .withBoundaryPoint(new Point2D(0, 0))
                .withBoundaryPoint(new Point2D(100, 0))
                .withBoundaryPoint(new Point2D(100, 100))
                .withBoundaryPoint(new Point2D(75, 100))
                .withBoundaryPoint(new Point2D(75, 50))
                .withBoundaryPoint(new Point2D(25, 50))
                .withBoundaryPoint(new Point2D(25, 100))
                .withBoundaryPoint(new Point2D(0, 100))
                .build();
    }

    private static Matcher<LineSegment2D> almostEqualTo(final LineSegment2D expectedValue) {
        return new BaseMatcher<LineSegment2D>() {
            public void describeTo(Description description) {
                description.appendValue(expectedValue);
            }

            public boolean matches(Object actual) {
                if (actual.getClass() != expectedValue.getClass()) { return false; }

                LineSegment2D actualLine = (LineSegment2D) actual;
                return actualLine.almostEquals(expectedValue, 0.001);
            }
        };
    }
}

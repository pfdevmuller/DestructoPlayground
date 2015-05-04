package za.co.pietermuller.playground.destructo;

import com.google.common.collect.ImmutableList;
import math.geom2d.Point2D;
import math.geom2d.domain.BoundaryPolyCurve2D;
import math.geom2d.domain.ContinuousOrientedCurve2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.Ray2D;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class WorldModel {

    private final BoundaryPolyCurve2D<? extends ContinuousOrientedCurve2D> outerBoundary;

    private WorldModel(BoundaryPolyCurve2D<? extends ContinuousOrientedCurve2D> outerBoundary) {
        this.outerBoundary = checkNotNull(outerBoundary, "outerBoundary is null!");
    }

    /**
     * Returns the line from the given point to the closest wall, in the given direction.
     *
     * @param sourcePosition
     * @param directionAngle in radians
     * @return a line from the given point, in the given direction, to the closest wall
     * @throws IllegalArgumentException if the point is not within the outer boundary of the world
     */
    public LineSegment2D getLineToNearestWall(final Point2D sourcePosition, double directionAngle) {
        checkArgument(containsPoint(sourcePosition), "sourcePosition is not within outer boundary of world");

        Ray2D ray = new Ray2D(sourcePosition, directionAngle);
        List<Point2D> intersections = ImmutableList.copyOf(outerBoundary.intersections(ray));

        checkArgument(intersections.size() > 0, "no wall found in direction of search - boundary must be broken");

        Point2D closestIntersection = intersections.get(0);
        double shortestDistance = sourcePosition.distance(closestIntersection);
        for (Point2D point : intersections) {
            double distance = sourcePosition.distance(point);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                closestIntersection = point;
            }
        }
        return new LineSegment2D(sourcePosition, closestIntersection);
    }

    /**
     * Checks if the given point lies withing the outer boundary of the world model.
     *
     * @param point2D
     * @return true if the given point lies withing the outer boundary of the world
     */
    public boolean containsPoint(Point2D point2D) {
        checkNotNull(point2D, "point2D is null!");
        return outerBoundary.isInside(point2D);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BoundaryPolyCurve2D<ContinuousOrientedCurve2D> outerBoundary;
        private Point2D firstPoint;
        private Point2D lastPoint;

        private Builder() {
            this.outerBoundary = new BoundaryPolyCurve2D<ContinuousOrientedCurve2D>();
        }

        public Builder withBoundaryCurve(ContinuousOrientedCurve2D curve) {
            outerBoundary.add((ContinuousOrientedCurve2D) curve.clone());
            return this;
        }

        /**
         * Adds a new line segment to the builder from the last point to the given point.
         *
         * The first add will not create a line segment. On build, a line segment will be
         * added between the last and the first points, if they exist and are not the same.
         *
         * @param point
         * @return
         */
        public Builder withBoundaryPoint(Point2D point) {
            if (lastPoint != null) {
                LineSegment2D line = new LineSegment2D(lastPoint, point);
                lastPoint = point;
                return withBoundaryCurve(line);
            } else {
                firstPoint = point;
                lastPoint = point;
                return this;
            }
        }

        public WorldModel build() {
            if (firstPoint != null) {
                if (firstPoint == lastPoint)
                    throw new IllegalStateException("only one boundary point given");
                // Connect last point to first point
                withBoundaryPoint(firstPoint);
            }

            checkArgument(outerBoundary.isClosed(), "outer boundary is not closed");
            return new WorldModel(outerBoundary);
        }
    }
}

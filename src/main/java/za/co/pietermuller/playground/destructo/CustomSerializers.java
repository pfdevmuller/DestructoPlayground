package za.co.pietermuller.playground.destructo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;

import java.io.IOException;

public class CustomSerializers {

    public static class Point2DSerializer extends StdSerializer<Point2D> {

        public Point2DSerializer(Class<Point2D> t) {
            super(t);
        }

        @Override
        public void serialize(Point2D point2D,
                              JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("x", point2D.x());
            jsonGenerator.writeNumberField("y", point2D.y());
            jsonGenerator.writeEndObject();
        }
    }

    public static class LineSegment2DSerializer extends StdSerializer<LineSegment2D> {

        public LineSegment2DSerializer(Class<LineSegment2D> t) {
            super(t);
        }

        @Override
        public void serialize(LineSegment2D lineSegment2D, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("point1", lineSegment2D.firstPoint());
            jsonGenerator.writeObjectField("point2", lineSegment2D.lastPoint());
            jsonGenerator.writeEndObject();
        }
    }

    public static class Box2DSerializer extends  StdSerializer<Box2D> {

        protected Box2DSerializer(Class<Box2D> t) {
            super(t);
        }

        @Override
        public void serialize(Box2D box2D, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("minX", box2D.getMinX());
            jsonGenerator.writeNumberField("maxX", box2D.getMaxX());
            jsonGenerator.writeNumberField("minY", box2D.getMinY());
            jsonGenerator.writeNumberField("maxY", box2D.getMaxY());
            jsonGenerator.writeEndObject();
        }
    }
}

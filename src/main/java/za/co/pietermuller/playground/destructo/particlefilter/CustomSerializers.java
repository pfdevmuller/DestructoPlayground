package za.co.pietermuller.playground.destructo.particlefilter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import math.geom2d.Point2D;

import java.io.IOException;

public class CustomSerializers {

    public static class Point2DSerializer extends StdSerializer<Point2D> {

        protected Point2DSerializer(Class<Point2D> t) {
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
}

package za.co.pietermuller.playground.destructo;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import fi.iki.elonen.NanoHTTPD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusServer extends NanoHTTPD {

    private final Map<String, StatusServable> servables = new HashMap<String, StatusServable>();
    private final List<OrderListener> orderListeners = new ArrayList<OrderListener>();

    public StatusServer() {
        super(8080);
    }

    public void addToServables(String key, StatusServable servable) {
        servables.put(key, servable);
    }

    public void addToOrderListeners(OrderListener orderListener) {
        orderListeners.add(orderListener);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();

        Response response = getDefaultResponse();

        if (method == Method.GET) {
            if (uri.endsWith("status")) {
                response = getStatusResponse();
            }
        } else if (method == Method.POST) {
            if (uri.endsWith("order")) {
                response = processOrder(session);
            } else if (uri.endsWith("quit")) {
                response = processQuit(session);
            } else if (uri.endsWith("testready")) {
                response =  new NanoHTTPD.Response("{\"response\" : \"yes\"}");
            }

        }

        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

    private Response getDefaultResponse() {
        return new NanoHTTPD.Response("{\"response\" : \"No idea what you just said.\"}");
    }

    private Response getStatusResponse() {
        String msg = "{ }";

        if (servables.size() > 0) {
            List<String> entries = new ArrayList<String>(servables.size());
            for (Map.Entry<String, StatusServable> entry : servables.entrySet()) {
                String status = entry.getValue().getStatus();
                entries.add(String.format("\"%s\": %s", entry.getKey(), status));
            }
            msg = "{\n" +
                  Joiner.on(",\n").join(entries) +
                  "}";
        }

        return new NanoHTTPD.Response(msg);
    }

    private Response processOrder(IHTTPSession session) {
        Map<String, String> params = session.getParms();

        Optional<Double> maybeDistance = getDistanceFromParams(params);
        Optional<Rotation> maybeRotation = getRotationFromParams(params);
        if (maybeDistance.isPresent() || maybeRotation.isPresent()) {
            double distance = maybeDistance.isPresent() ? maybeDistance.get() : 0;
            Rotation rotation = maybeRotation.isPresent() ? maybeRotation.get() : Rotation.noRotation();
            Movement movement = new Movement(distance, rotation);
            for (OrderListener orderListener : orderListeners) {
                orderListener.addOrder(movement);
            }
        }

        return new NanoHTTPD.Response("{\"response\" : \"Added order.\"}");
    }

    private Optional<Double> getDistanceFromParams(Map<String, String> params) {
        if (params.containsKey("distance")) {
            double distance = Double.parseDouble(params.get("distance"));
            return Optional.of(distance);
        }
        return Optional.absent();
    }

    private Optional<Rotation> getRotationFromParams(Map<String, String> params) {
        if (params.containsKey("degrees")) {
            double degrees = Double.parseDouble(params.get("degrees"));
            return Optional.of(Rotation.degrees(degrees));
        } else if (params.containsKey("radians")) {
            double radians = Double.parseDouble(params.get("radians"));
            return Optional.of(Rotation.radians(radians));
        }
        return Optional.absent();
    }

    private Response processQuit(IHTTPSession session) {
        for (OrderListener orderListener : orderListeners) {
            orderListener.quit();
        }
        return new NanoHTTPD.Response("{\"response\" : \"Quiting.\"}");
    }
}

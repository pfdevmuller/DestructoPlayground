package za.co.pietermuller.playground.destructo;

import com.google.common.base.Joiner;
import fi.iki.elonen.NanoHTTPD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusServer extends NanoHTTPD {

    private int stepNumber = 0;

    private Map<String, StatusServable> servables;

    public StatusServer() {
        super(8080);
        this.servables = new HashMap<String, StatusServable>();
    }

    public void addtoServables(String key, StatusServable servable) {
        servables.put(key, servable);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();

        String msg = "<html><body><h1>Destructo Status Server</h1>\n";
        msg += "<p>Hello from Destructo. Method: " + method + ", uri: " + uri + "</p>";
        msg += "<p>Step Number: " + stepNumber + "</p>";
        msg += "<p>Time on Server: " + System.currentTimeMillis() + "</p>";
        msg += "</body></html>\n";

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

        Response response = new NanoHTTPD.Response(msg);
        response.addHeader("Access-Control-Allow-Origin", "*");

        return response;
    }

    public void incrementStepNumber() {
        this.stepNumber++;
    }
}

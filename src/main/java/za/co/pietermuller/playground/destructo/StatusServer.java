package za.co.pietermuller.playground.destructo;

import fi.iki.elonen.NanoHTTPD;

public class StatusServer extends NanoHTTPD {

    private int stepNumber = 0;

    public StatusServer() {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();

        String msg = "<html><body><h1>Destructo Status Server</h1>\n";
        msg += "<p>Hello from Destructo. Method: " + method + ", uri: " + uri + "</p>";
        msg += "<p>Step Number: " + stepNumber + "</p>";
        msg += "</body></html>\n";

        return new NanoHTTPD.Response(msg);
    }

    public void incrementStepNumber() {
        this.stepNumber++;
    }
}

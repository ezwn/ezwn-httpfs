package httpfs.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

/**
 *
 * @author Nicolas
 */
public class DiagnosticHandler implements Handler {

    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) throws UnhandledRequestSignal {
        String uri = String.valueOf(session.getUri());

        if (uri.endsWith("/@diagnostic")) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return newFixedLengthResponse(mapper.writeValueAsString(new Diagnostic()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        throw new UnhandledRequestSignal();
    }

}

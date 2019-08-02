
package httpfs.server;

import fi.iki.elonen.NanoHTTPD;
import static fi.iki.elonen.NanoHTTPD.newChunkedResponse;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;
import java.io.InputStream;

/**
 *
 * @author nicolas
 */
public class ClassPathHandler implements Handler {
    
    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) throws UnhandledRequestSignal {
        
        String uri = String.valueOf(session.getUri());
        String method = String.valueOf(session.getMethod()).toUpperCase();

        System.out.println(method + " " + uri + " handled by " + getClass().getSimpleName());
        
        if (uri.startsWith("/web/")) {
            switch (method) {
                case "GET":
                        InputStream inputStream = NanoRestFileServer.class.getResourceAsStream(uri);
                        String mimeType = MimeUtil.getMimeType(uri);
                        
                        return newChunkedResponse(NanoHTTPD.Response.Status.OK, mimeType, inputStream);
            }
            
            return newFixedLengthResponse(NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED, null, null);
        }
        
        throw new UnhandledRequestSignal();
    }
    
}

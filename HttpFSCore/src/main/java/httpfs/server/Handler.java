package httpfs.server;

import fi.iki.elonen.NanoHTTPD;

/**
 *
 * @author Nicolas ENZWEILER
 */
public interface Handler {

    NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) throws UnhandledRequestSignal;
    
}

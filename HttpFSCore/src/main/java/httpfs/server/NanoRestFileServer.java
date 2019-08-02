package httpfs.server;

import httpfs.HttpFSLogger;
import fi.iki.elonen.NanoHTTPD;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLServerSocketFactory;

public class NanoRestFileServer extends NanoHTTPD {

    private final List<Handler> handlers = new ArrayList();
    private final Handler diagnosticHandler;
    private final Handler mainHandler;

    public NanoRestFileServer(int port, String fsPath, boolean https) throws FileNotFoundException {
        super(port);

        if (https)
            enableHTTPS();

        diagnosticHandler = new DiagnosticHandler();
        mainHandler = new FileSystemHandler(fsPath, "/");
    }

    private void enableHTTPS() {
        HttpFSLogger.log("* Enabling HTTPS");

        try {
            SSLServerSocketFactory factory
                    = NanoRestFileServer.makeSSLSocketFactory("/keystore.p12", "httpfs2482".toCharArray(), "PKCS12");

            setServerSocketFactory(new SecureServerSocketFactory(factory, null));
        } catch (IOException ex) {
            Logger.getLogger(NanoRestFileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static SSLServerSocketFactory makeSSLSocketFactory(String keyAndTrustStoreClasspathPath, char[] passphrase, String keyStoreType) throws IOException {
        try {
            KeyStore keystore = KeyStore.getInstance(keyStoreType);
            InputStream keystoreStream = NanoHTTPD.class.getResourceAsStream(keyAndTrustStoreClasspathPath);

            if (keystoreStream == null) {
                throw new IOException("Unable to load keystore from classpath: " + keyAndTrustStoreClasspathPath);
            }

            keystore.load(keystoreStream, passphrase);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, passphrase);
            return makeSSLSocketFactory(keystore, keyManagerFactory);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public void addHandler(Handler handler) {
        this.handlers.add(handler);
    }

    @Override
    public Response serve(IHTTPSession session) {

        String uri = String.valueOf(session.getUri());
        // String method = String.valueOf(session.getMethod()).toUpperCase();

        if (uri.equals("/favicon.ico")) {
            return null;
        }

        Response response = null;

        try {
            response = diagnosticHandler.handle(session);
        } catch (UnhandledRequestSignal urs) {
            //urs.printStackTrace();
        }

        for (Handler handler : handlers) {
            if (response == null) {
                try {
                    response = handler.handle(session);
                } catch (UnhandledRequestSignal urs) {
                    //urs.printStackTrace();
                }
            }
        }

        if (response == null) {
            try {
                response = mainHandler.handle(session);
            } catch (UnhandledRequestSignal urs) {
                //urs.printStackTrace();
            }
        }

        if (response != null) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Max-Age", "3628800");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, MOVE, OPTIONS, PUT, DELETE");
            response.addHeader(
                    "Access-Control-Allow-Headers",
                    "X-Requested-With, Authorization, content-type, Destination, Overwrite"
            );
            
            return response;
        }

        throw new RuntimeException("Unhandled request");

    }

}

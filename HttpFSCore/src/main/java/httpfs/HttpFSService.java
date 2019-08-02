package httpfs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import httpfs.server.NanoRestFileServer;

/**
 *
 * @author nicolas
 */
public class HttpFSService {

    public static void boot(final String rootPath, final int port, final boolean https) {

        HttpFSLogger.log("");
        HttpFSLogger.log("Welcome to HttpFS webserver");
        HttpFSLogger.log("");
        HttpFSLogger.log("> File system root: " + rootPath);
        HttpFSLogger.log("> Server port: " + port);
        HttpFSLogger.log("");

        updateInstall(rootPath);

        try {
            startServer(rootPath, port, https);
        } catch (FileNotFoundException fne) {
        }
    }

    private static void updateInstall(final String rootPath) {
        HttpFSLogger.log("* updating installation");

        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdirs();
        }

//        File web = new File(root, "web");
//        if (!web.exists()) {
//            web.mkdirs();
//        }
//
//        File data = new File(root, "data");
//        if (!data.exists()) {
//            data.mkdirs();
//        }
    }

    private static void startServer(final String rootPath, final int port, final boolean https) throws FileNotFoundException {
        HttpFSLogger.log("* starting webserver");

        NanoRestFileServer server
                = new NanoRestFileServer(port, rootPath, https);

        try {
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpFSService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

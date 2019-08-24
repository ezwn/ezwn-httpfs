package httpfs.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpfs.HttpFSLogger;
import fi.iki.elonen.NanoHTTPD;
import static fi.iki.elonen.NanoHTTPD.newChunkedResponse;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import httpfs.file.api.FileAPI;
import httpfs.file.api.FileEvent;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 *
 * @author Nicolas
 */
public class FileSystemHandler implements Handler {

    private final String point;
    private final File root;

    public FileSystemHandler(String fsPath, String point) throws FileNotFoundException {
        this.root = new File(fsPath);

        if (!this.root.exists()) {
            throw new FileNotFoundException();
        }

        this.point = point;
    }

    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) throws UnhandledRequestSignal {

        String uri = String.valueOf(session.getUri());
        final String method = String.valueOf(session.getMethod()).toUpperCase();

        final String startsWith = point.equals("/") ? "/" : point + "/";

        if (!uri.equals(point) && !uri.startsWith(startsWith)) {
            throw new UnhandledRequestSignal();
        }

        uri = correctUri(uri);

        HttpFSLogger.log(
                method + " " + new File(root, uri).getAbsolutePath() + " (handled by " + getClass().getSimpleName() + ")"
        );

        try {
            NanoHTTPD.Response response;

            switch (method) {
                case "GET":
                    if (uri.endsWith("/@desc")) {
                        response = getFileDesc(uri.substring(0, uri.length() - 5));
                    } else if (uri.endsWith("/@list")) {
                        response = newFixedLengthResponse(
                                NanoHTTPD.Response.Status.OK,
                                "application/json",
                                FileAPI.listFiles(root, uri.substring(0, uri.length() - 5))
                        );
                    } else {
                        String mimeType = MimeUtil.getMimeType(uri);
                        // HttpFSLogger.log("mimeType=" + mimeType);
                        response = newChunkedResponse(
                                NanoHTTPD.Response.Status.OK,
                                mimeType,
                                new FileInputStream(new File(root, uri))
                        );
                    }
                    break;
                case "HEAD":
                    response = getFileDesc(uri);
                    break;
                case "POST":
                    response = saveFile(uri, session);
                    break;
                case "PUT":
                    response = createFolder(uri);
                    break;
                case "DELETE":
                    response = deleteFile(uri, session);
                    break;
                case "MOVE":
                    response = moveFile(uri, session);
                    break;
                case "OPTIONS":
                    response = newFixedLengthResponse("");
                    response.addHeader("content-type", "application/json");
                    break;
                default:
                    response = newFixedLengthResponse(NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED, null, null);
            }

            return response;
        } catch (FileNotFoundException fnfe) {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, null, null);
        } catch (IOException ex) {
            Logger.getLogger(NanoRestFileServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", ex.getMessage());
        } catch (NanoHTTPD.ResponseException ex) {
            Logger.getLogger(NanoRestFileServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", ex.getMessage());
        }

    }

    private String correctUri(String uri) {
        if (!point.equals("/")) {
            uri = uri.substring(point.length());
        }
        return uri;
    }

    private NanoHTTPD.Response getFileDesc(String path) throws FileNotFoundException, IOException {
        return newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK,
                "application/json",
                FileAPI.getFileDesc(root, path)
        );
    }

    private NanoHTTPD.Response saveFile(String uri, NanoHTTPD.IHTTPSession session) throws IOException, NanoHTTPD.ResponseException {
        NanoHTTPD.Response response = null;

        File file = new File(root, uri);
        // HttpFSLogger.log("target:" + file.getAbsolutePath());
        final FileOutputStream out = new FileOutputStream(file);
        final InputStream in = session.getInputStream();

        final String contentLengthStr = session.getHeaders().get("content-length");
        int contentLength = Integer.parseInt(contentLengthStr);

        try {
            // HttpFSLogger.log("Trying to read " + contentLength + " bytes.");
            byte[] buffer = new byte[contentLength];

            while (contentLength > 0) {
                int len = in.read(buffer);
                // HttpFSLogger.log(len + " bytes read.");
                out.write(buffer, 0, len);
                contentLength -= len;
            }

            // HttpFSLogger.log("Sending response");
            ObjectMapper mapper = new ObjectMapper();
            response = newFixedLengthResponse(
                    mapper.writeValueAsString(new FileEvent(false, "FILE_SAVED", file))
            );
            response.addHeader("content-type", "application/json");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // HttpFSLogger.log("Closing file");
            // in.close();
            out.close();
        }

        return response;
    }

    private NanoHTTPD.Response moveFile(String uri, NanoHTTPD.IHTTPSession session) throws JsonProcessingException {
        NanoHTTPD.Response response;

        String newPath;

        try {
            newPath = correctUri(URLDecoder.decode(
                    session.getHeaders().get("destination"),
                    "UTF-8"
            ));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            response = newFixedLengthResponse(mapper.writeValueAsString(
                    FileAPI.moveFile(root, uri, newPath)
            ));
            response.addHeader("content-type", "application/json");
        } catch (Exception exception) {
            response = newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "plain/text", "");
        }
        return response;
    }

    private NanoHTTPD.Response deleteFile(String uri, NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.Response response;
        try {
            ObjectMapper mapper = new ObjectMapper();
            response = newFixedLengthResponse(mapper.writeValueAsString(
                    FileAPI.deleteFile(root, uri)
            ));
            response.addHeader("content-type", "application/json");
        } catch (Exception exception) {
            response = newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "plain/text", "");
        }
        return response;
    }

    private NanoHTTPD.Response createFolder(String uri) throws FileNotFoundException, IOException {
        NanoHTTPD.Response response;
        try {
            ObjectMapper mapper = new ObjectMapper();
            response = newFixedLengthResponse(mapper.writeValueAsString(
                    FileAPI.createFolder(root, uri)
            ));
            response.addHeader("content-type", "application/json");
        } catch (Exception exception) {
            response = newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "plain/text", "");
        }
        return response;
    }

}

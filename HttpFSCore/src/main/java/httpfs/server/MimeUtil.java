package httpfs.server;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nicolas ENZWEILER
 */
public class MimeUtil {

    public static final Map<String, String> mimeTypes = new HashMap<>();

    static {
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "text/javascript");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("svg", "image/svg+xml; charset=UTF-8");
        mimeTypes.put("map", "text/plain");
        mimeTypes.put("md", "text/plain");
        mimeTypes.put("csv", "text/plain; charset=UTF-8");
        mimeTypes.put("mp3", "audio/x-mpeg-3");
        mimeTypes.put("wav", "audio/x-wav");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("csv", "text/plain");
        mimeTypes.put("txt", "text/plain");
    }
    
    public static String getMimeType(final String fileName) {
        final int o = fileName.lastIndexOf(".");
        
        if (o==-1)
            return "text/plain";
        else {
            final String ext = fileName.substring(o+1);
            final String mimeType = MimeUtil.mimeTypes.get(ext);
            if (mimeType==null) {
                System.out.println("Unrecognize mime type ." + ext);
                return "text/plain";
            } else
                return mimeType;
        }
    }

}

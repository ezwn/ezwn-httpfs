package httpfs.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import fi.iki.elonen.NanoHTTPD;
import httpfs.HttpFSLogger;

public class NanoHTTPFileServer extends NanoHTTPD {

	private final List<Handler> handlers = new ArrayList<>();
	private Handler diagnosticHandler;
	private Handler mainHandler;
	private String fsPath;
	private int port;

	public NanoHTTPFileServer(int port, String fsPath) throws FileNotFoundException {
		super(port);

		this.fsPath = fsPath;
		this.port = port;
	}

	private static void updateInstall(final String rootPath) {
		HttpFSLogger.log("* updating installation");

		File root = new File(rootPath);
		if (!root.exists()) {
			root.mkdirs();
		}
	}

	@PostConstruct
	public void init() throws FileNotFoundException {
		sayHello();
		
		updateInstall(fsPath);

		HttpFSLogger.log("* starting webserver");

		diagnosticHandler = new DiagnosticHandler();
		mainHandler = new FileSystemHandler(fsPath, "/");
		
	}
	
	protected void sayHello() {
		HttpFSLogger.log("");
		HttpFSLogger.log("Welcome to HttpFS webserver");
		HttpFSLogger.log("");
		HttpFSLogger.log("> File system root: " + fsPath);
		HttpFSLogger.log("> Server port: " + port);
		HttpFSLogger.log("");
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
			// urs.printStackTrace();
		}

		for (Handler handler : handlers) {
			if (response == null) {
				try {
					response = handler.handle(session);
				} catch (UnhandledRequestSignal urs) {
					// urs.printStackTrace();
				}
			}
		}

		if (response == null) {
			try {
				response = mainHandler.handle(session);
			} catch (UnhandledRequestSignal urs) {
				// urs.printStackTrace();
			}
		}

		if (response != null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("Access-Control-Max-Age", "3628800");
			response.addHeader("Access-Control-Allow-Methods", "GET, POST, MOVE, OPTIONS, PUT, DELETE");
			response.addHeader("Access-Control-Allow-Headers",
					"X-Requested-With, Authorization, content-type, Destination, Overwrite");

			return response;
		}

		throw new RuntimeException("Unhandled request");

	}

}

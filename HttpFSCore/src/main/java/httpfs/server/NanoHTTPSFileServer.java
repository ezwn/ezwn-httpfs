package httpfs.server;

import httpfs.HttpFSLogger;
import fi.iki.elonen.NanoHTTPD;

import java.io.File;
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

public class NanoHTTPSFileServer extends NanoHTTPFileServer {

	public NanoHTTPSFileServer(int port, String fsPath) throws FileNotFoundException {
		super(port, fsPath);
	}

	@Override
	public void init() throws FileNotFoundException {
		super.init();
		enableHTTPS();
	}

	private void enableHTTPS() {
		HttpFSLogger.log("* Enabling HTTPS");

		try {
			SSLServerSocketFactory factory = NanoHTTPSFileServer.makeSSLSocketFactory("/keystore.p12",
					"httpfs2482".toCharArray(), "PKCS12");

			setServerSocketFactory(new SecureServerSocketFactory(factory, null));
		} catch (IOException ex) {
			Logger.getLogger(NanoHTTPSFileServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static SSLServerSocketFactory makeSSLSocketFactory(String keyAndTrustStoreClasspathPath, char[] passphrase,
			String keyStoreType) throws IOException {
		try {
			KeyStore keystore = KeyStore.getInstance(keyStoreType);
			InputStream keystoreStream = NanoHTTPD.class.getResourceAsStream(keyAndTrustStoreClasspathPath);

			if (keystoreStream == null) {
				throw new IOException("Unable to load keystore from classpath: " + keyAndTrustStoreClasspathPath);
			}

			keystore.load(keystoreStream, passphrase);
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keystore, passphrase);
			return makeSSLSocketFactory(keystore, keyManagerFactory);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
}

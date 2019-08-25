package httpfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import httpfs.server.NanoHTTPFileServer;

@SpringBootApplication
@ComponentScan({ "httpfs" })
@ConfigurationProperties(prefix = "httpfs")
@ImportResource("classpath:app-config.xml")
public class Application implements CommandLineRunner {

	@Autowired
	private NanoHTTPFileServer server;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) {
		
		try {
				server.start();
		} catch (FileNotFoundException fne) {
			
		} catch (IOException ex) {
			Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		try {
			while (true) {
				Thread.sleep(250);
			}
		} catch (InterruptedException ex) {
			Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}

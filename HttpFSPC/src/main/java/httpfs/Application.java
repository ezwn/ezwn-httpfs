package httpfs;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
    "httpfs"
})
@ConfigurationProperties(prefix = "httpfs")
public class Application implements CommandLineRunner {

    private String port = "5000";
    private String root = "~/httpfs";
    private String https = "false";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        HttpFSService.boot(getRoot(), Integer.parseInt(getPort()), Boolean.parseBoolean(getHttps()));

        try {
            while (true) {
                Thread.currentThread().sleep(250);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return the root
     */
    public String getRoot() {
        if (root.startsWith("~/")) {
            return System.getProperty("user.home") + root.substring(1);
        }

        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(String root) {
        this.root = root;
    }

    /**
     * @return the https
     */
    public String getHttps() {
        return https;
    }

    /**
     * @param https the https to set
     */
    public void setHttps(String https) {
        this.https = https;
    }

}

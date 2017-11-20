import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.AppConfig;

import java.io.File;

/**
 * Created by Evgeniy Slobozheniuk on 17.11.17.
 */
public class AppInstance {
    private static final Logger log = LogManager.getLogger(AppInstance.class);

    public static void main(String[] args) {
        log.info("Starting app...");
        AppConfig config = new AppConfig("config.properties");
        File keyFile = new File(AppInstance.class.getResource(config.getCertificateFile()).getPath());
        Client client = new Client(config, keyFile);
        client.login();
        client.logout();
    }
}

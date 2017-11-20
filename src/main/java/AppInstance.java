import login.HttpClientSSO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.AppConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Evgeniy Slobozheniuk on 17.11.17.
 */
public class AppInstance {
    private static final Logger log = LogManager.getLogger(AppInstance.class);

    public static void main(String[] args) {
        log.info("Starting app...");
        AppConfig config = new AppConfig("config.properties");
        File keyFile = new File(AppInstance.class.getResource("slobevg.p12").getPath());
        HttpClientSSO client = new HttpClientSSO(config, keyFile);
    }
}

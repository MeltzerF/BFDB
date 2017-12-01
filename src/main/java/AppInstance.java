import entities.Event;
import entities.EventType;
import entities.MarketFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import requests.EventRequest;
import requests.EventTypeRequest;
import utils.AppConfig;
import utils.Client;
import utils.SQLiteConnection;

import javax.swing.text.MaskFormatter;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Evgeniy Slobozheniuk on 17.11.17.
 */
public class AppInstance {
    private static final Logger log = LogManager.getLogger(AppInstance.class);

    public static void main(String[] args) {
        log.info("Starting app...");
        AppConfig config = new AppConfig("config.properties");
        File keyFile = new File(AppInstance.class.getResource(config.getCertificbateFile()).getPath());
        Client client = new Client(config, keyFile);
        SQLiteConnection connection = SQLiteConnection.getInstance(config);
        client.login();

        client.logout();
    }
}

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import entities.MarketCatalogue;
import operations.BusinessLogic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.AppCache;
import utils.AppConfig;
import utils.Client;
import utils.SQLiteConnection;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Evgeniy Slobozheniuk on 17.11.17.
 */
public class AppInstance {
    private static final Logger log = LogManager.getLogger(AppInstance.class);

    public static void main(String[] args) {
        log.info("Starting app...");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        AppConfig config = new AppConfig("config.properties");
        File keyFile = new File(AppInstance.class.getResource(config.getCertificateFile()).getPath());
        Client client = new Client(config, keyFile);
        SQLiteConnection connection = SQLiteConnection.getInstance(config);
        AppCache appCache = new AppCache();
        client.login();
        BusinessLogic businessLogic = new BusinessLogic(client, connection, appCache);
        businessLogic.init();
    }
}

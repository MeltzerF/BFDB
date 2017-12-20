import com.mchange.v2.c3p0.ComboPooledDataSource;
import database.EventDAO;
import database.EventTypeDAO;
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

import java.io.File;
import java.util.Date;
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
        MarketFilter filter = new MarketFilter();
        Set<String> set = new HashSet<>();
        set.add("1");
        EventTypeDAO edao = new EventTypeDAO(connection);
        EventTypeRequest ereq = new EventTypeRequest(filter, client);
        for (EventType arg : ereq.getObjects()) {
            edao.InsertOrUpdate(arg);
        }
        EventDAO eventDAO = new EventDAO(connection);
        filter.setEventTypeIds(set);
        EventRequest eventRequest = new EventRequest(filter, client);
        for (Event event : eventRequest.getObjects()) {
            eventDAO.InsertOrUpdate(event);
        }
        client.logout();
    }
}

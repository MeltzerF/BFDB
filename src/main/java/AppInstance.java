import entities.Event;
import entities.EventType;
import entities.MarketFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import requests.EventRequest;
import requests.EventTypeRequest;
import utils.AppConfig;
import utils.Client;

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
        File keyFile = new File(AppInstance.class.getResource(config.getCertificateFile()).getPath());
        Client client = new Client(config, keyFile);
        client.login();
        MarketFilter filter = new MarketFilter();
        EventTypeRequest etr = new EventTypeRequest(filter, client);
        List<EventType> list= etr.getObjects();
        for (EventType eventType : list) {
            log.info(eventType.toString());
        }
        Set<String> set = new HashSet<>();
        set.add("1");
        filter.setEventTypeIds(set);
        EventRequest er = new EventRequest(filter, client);
        List<Event> eventlist = er.getObjects();
        for (Event event : eventlist) {
            log.info(event.toString());
        }
        client.logout();

    }
}

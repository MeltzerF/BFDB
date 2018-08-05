package operations;

import com.google.common.collect.Lists;
import database.EventDAO;
import database.EventTypeDAO;
import database.MarketBookDAO;
import database.MarketCatalogueDAO;
import entities.*;
import enums.GroupType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import requests.EventRequest;
import requests.EventTypeRequest;
import requests.MarketBookRequest;
import requests.MarketCatalogueRequest;
import utils.AppCache;
import utils.Client;
import utils.SQLiteConnection;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Evgeniy Slobozheniuk on 19.03.2018.
 */
public class BusinessLogic {
    private static final Logger log = LogManager.getLogger(BusinessLogic.class);

    private MarketFilter marketFilter;
    private EventTypeDAO eventTypeDAO;
    private EventDAO eventDAO;
    private MarketCatalogueDAO marketCatalogueDAO;
    private MarketBookDAO marketBookDAO;
    private EventTypeRequest eventTypeRequest;
    private EventRequest eventRequest;
    private MarketCatalogueRequest marketCatalogueRequest;
    private MarketBookRequest marketBookRequest;
    private AppCache appCache;
    private SQLiteConnection connectionPool;

    private ScheduledExecutorService scheduledExecutorService;

    public BusinessLogic(Client client, SQLiteConnection connectionPool, AppCache appCache) {
        this.marketFilter = new MarketFilter();
        this.eventTypeDAO = new EventTypeDAO(connectionPool);
        this.eventDAO = new EventDAO(connectionPool);
        this.marketCatalogueDAO = new MarketCatalogueDAO(connectionPool);
        this.marketBookDAO = new MarketBookDAO(connectionPool);
        this.eventTypeRequest = new EventTypeRequest(client);
        this.eventRequest = new EventRequest(client);
        this.marketCatalogueRequest = new MarketCatalogueRequest(client);
        this.marketBookRequest = new MarketBookRequest(client);
        this.appCache = appCache;
        this.connectionPool = connectionPool;

        this.scheduledExecutorService = Executors.newScheduledThreadPool(5);
    }

    public void init() {
        initialLoad();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            MarketBooksJob(GroupType.LIVE);
        }, 2, 1, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            MarketBooksJob(GroupType.FIRST);
        }, 2, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            MarketBooksJob(GroupType.SECOND);
        }, 2, 20, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            MarketBooksJob(GroupType.THIRD);
        }, 2, 60, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            MarketBooksJob(GroupType.ZERO);
        }, 2, 100, TimeUnit.SECONDS);
    }

    private void MarketBooksJob(GroupType groupType) {
        log.info("Getting games for " + groupType);
        List<String> marketIdList = appCache.getMarketIdFromGroup(groupType);
        if (!marketIdList.isEmpty()) {
            for (MarketBook marketBook : getMarketBooks(marketIdList)) {
                marketBookDAO.InsertOrUpdate(marketBook);
            }
        } else {
            log.warn(groupType + " cache is empty!");
        }
    }

    private List<MarketBook> getMarketBooks(List<String> marketIdList) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<MarketBook> marketBookList = new ArrayList<>();
        Lists.partition(marketIdList, 11).forEach(marketIdBatch -> executorService.execute(() -> {
            marketBookList.addAll(marketBookRequest.getObjects(marketIdBatch));
        }));
        executorService.shutdown();
        try {
            final boolean isDone = executorService.awaitTermination(1, TimeUnit.MINUTES);
            for (MarketBook marketBook : marketBookList) {
                appCache.addToCache(new GameEvent(marketBook));
                if (marketBook.getInplay()) {
                    marketBook.setHomeTeamScore();
                    marketBook.setGuestTeamScore();
                }
            }
            log.info("Market Book loading " + (isDone ? "is finished" : "not finished") + ", entries in list - " + marketBookList.size());
            return marketBookList;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getMarketCatalogues() {
        List<String> eventIdList = new ArrayList<>();
        Set<String> marketTypeCodeSet = new HashSet<>();
        marketTypeCodeSet.add("MATCH_ODDS");

        Calendar date = new GregorianCalendar(); //Getting 00:00:00 of a current day
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        for (Event event : eventDAO.Select(date.getTime()))  {
            eventIdList.add(event.getId());
        }
        int numberOfSubSets = 200 / marketTypeCodeSet.size();
        List<List<String>> listOfSubSets = Lists.partition(eventIdList, numberOfSubSets);

        for (List<String> list : listOfSubSets) {
            Set<String> eventIdSet = new HashSet<>(list);
            marketFilter.setEventIds(eventIdSet);
            log.debug(eventIdSet.size() + " games are added to the set");
            marketFilter.setMarketTypeCodes(marketTypeCodeSet);
            for (MarketCatalogue marketCatalogue : marketCatalogueRequest.getObjects(marketFilter)) {
                appCache.addToMarketCatalogueCache(marketCatalogue);
                marketCatalogueDAO.InsertOrUpdate(marketCatalogue);
            }
        }
        log.info("Market Catalogue loading is finished, entries in MarketCatalogue cache - " + appCache.getAllKeysFromMarketCatalogueCache().size());
    }

    private void getEvents() {
        Set<String> set = new HashSet<>();
        set.add("1");
        marketFilter.setEventTypeIds(set);
        for (Event event : eventRequest.getObjects(marketFilter)) {
            eventDAO.InsertOrUpdate(event);
        }
        log.info("Event loading is finished");
    }

    private void getEventTypes() {
        for (EventType arg : eventTypeRequest.getObjects(marketFilter)) {
            eventTypeDAO.InsertOrUpdate(arg);
        }
    }

    private void initialLoad() {
        getEventTypes();
        getEvents();
        getMarketCatalogues();
        getMarketBooks(appCache.getAllKeysFromMarketCatalogueCache());
    }
}

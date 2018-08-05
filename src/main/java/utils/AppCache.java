package utils;

import entities.GameEvent;
import entities.MarketCatalogue;
import enums.GroupType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Evgeniy Slobozheniuk on 04.04.2018.
 */
public class AppCache {
    private static final Logger log = LogManager.getLogger(AppCache.class);

    ConcurrentHashMap<String, GameEvent> eventCache;
    ConcurrentHashMap<String, MarketCatalogue> marketCatalogueCache;

    public AppCache() {
        this.eventCache = new ConcurrentHashMap<>();
        this.marketCatalogueCache = new ConcurrentHashMap<>();
    }

    public void addToCache(GameEvent gameEvent) {
        eventCache.put(gameEvent.getMarketId(), gameEvent);
    }

    public void removeFromCaches(String marketId) {
        eventCache.remove(marketId);
        marketCatalogueCache.remove(marketId);
    }

    public GameEvent getFromCache(String marketId) {
        return eventCache.get(marketId);
    }

    public MarketCatalogue getMarketCatalogueFromCache(String marketId) {
        return marketCatalogueCache.get(marketId);
    }
    public List<GameEvent> getObjectsFromCache() {
        return new ArrayList<GameEvent>(eventCache.values());
    }

    public List<String> getKeysFromCache() {
        return new ArrayList<>(eventCache.keySet());
    }

    public List<String> getMarketIdFromGroup(GroupType groupType) {
        switch (groupType) {
            case LIVE:
                return new ArrayList<>(eventCache.values().stream()
                        .filter(value -> value.isInPlay())
                        .map(GameEvent::getMarketId)
                        .collect(Collectors.toList()));
            case FIRST:
                return new ArrayList<>(eventCache.values().stream()
                        .filter(value -> value.getTotalMatched() + value.getTotalAvailable() >= 100000)
                        .map(GameEvent::getMarketId)
                        .collect(Collectors.toList()));
            case SECOND:
                return new ArrayList<>(eventCache.values().stream()
                        .filter(value -> value.getTotalMatched() + value.getTotalAvailable() < 100000)
                        .filter(value -> value.getTotalMatched() + value.getTotalAvailable() >= 50000)
                        .map(GameEvent::getMarketId)
                        .collect(Collectors.toList()));
            case THIRD:
                return new ArrayList<>(eventCache.values().stream()
                        .filter(value -> value.getTotalMatched() + value.getTotalAvailable() < 50000)
                        .filter(value -> value.getTotalMatched() + value.getTotalAvailable() >= 10000)
                        .map(GameEvent::getMarketId)
                        .collect(Collectors.toList()));
            case ZERO:
                return new ArrayList<>(eventCache.values().stream()
                    .filter(value -> value.getTotalMatched() + value.getTotalAvailable() < 10000)
                    .map(GameEvent::getMarketId)
                    .collect(Collectors.toList()));
            default:
                log.error("No case was chosen");
                return null;
        }
    }


    public Boolean eventCacheIsEmpty() {
        return eventCache.isEmpty();
    }

    public void addToMarketCatalogueCache(MarketCatalogue marketCatalogue) {
        marketCatalogueCache.put(marketCatalogue.getMarketId(), marketCatalogue);
    }

    public MarketCatalogue getFromMarketCatalogueCache(String marketId) {
        return marketCatalogueCache.get(marketId);
    }

    public List<String> getAllKeysFromMarketCatalogueCache() {
        return new ArrayList<>(marketCatalogueCache.keySet());
    }

    public void clearEventCache() {
        eventCache.clear();
    }

    public boolean isInGameEventCache(String marketId) {
        return eventCache.containsKey(marketId);
    }
}

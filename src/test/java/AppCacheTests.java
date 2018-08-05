import entities.MarketCatalogue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import utils.AppCache;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Evgeniy Slobozheniuk on 19.04.2018.
 */
public class AppCacheTests {
    private static final Logger log = LogManager.getLogger(AppCacheTests.class);
    AppCache appCache = new AppCache();

    @After
    public void Clear() {
        appCache.clearEventCache();
    }

    @Test
    public void loadToMarketCatalogueCache() {
        MarketCatalogue marketCatalogue = new MarketCatalogue();
        marketCatalogue.setMarketId("1.123456789");
        marketCatalogue.setTotalMatched(123456);
        appCache.addToMarketCatalogueCache(marketCatalogue);
        Assert.assertEquals(marketCatalogue.getMarketId(), appCache.getFromMarketCatalogueCache("1.123456789").getMarketId());
        Assert.assertEquals(marketCatalogue.getTotalMatched(), appCache.getFromMarketCatalogueCache("1.123456789").getTotalMatched(), 0);
    }

    @Test
    public void loadTwiceSameObjectToMarketCatalogueCache() {
        MarketCatalogue marketCatalogue = new MarketCatalogue();
        marketCatalogue.setMarketId("1.123456789");
        marketCatalogue.setTotalMatched(123456);
        appCache.addToMarketCatalogueCache(marketCatalogue);
        MarketCatalogue marketCatalogueCopy = new MarketCatalogue();
        marketCatalogueCopy.setMarketId("1.123456789");
        marketCatalogueCopy.setTotalMatched(123456);
        appCache.addToMarketCatalogueCache(marketCatalogueCopy);
        Assert.assertEquals(marketCatalogue.getMarketId(), appCache.getFromMarketCatalogueCache("1.123456789").getMarketId());
        Assert.assertEquals(marketCatalogue.getTotalMatched(), appCache.getFromMarketCatalogueCache("1.123456789").getTotalMatched(), 0);
    }

    @Test
    public void checkThatObjectIsReplacedOnAdding() {
        MarketCatalogue marketCatalogue = new MarketCatalogue();
        marketCatalogue.setMarketId("1.123456789");
        marketCatalogue.setTotalMatched(123456);
        appCache.addToMarketCatalogueCache(marketCatalogue);
        MarketCatalogue marketCatalogueCopy = new MarketCatalogue();
        marketCatalogueCopy.setMarketId("1.123456789");
        marketCatalogueCopy.setTotalMatched(345678);
        appCache.addToMarketCatalogueCache(marketCatalogueCopy);
        Assert.assertEquals(marketCatalogueCopy.getMarketId(), appCache.getFromMarketCatalogueCache("1.123456789").getMarketId());
        Assert.assertEquals(marketCatalogueCopy.getTotalMatched(), appCache.getFromMarketCatalogueCache("1.123456789").getTotalMatched(), 0);
        log.info("Total matched: " + appCache.getFromMarketCatalogueCache("1.123456789").getTotalMatched());
    }
}

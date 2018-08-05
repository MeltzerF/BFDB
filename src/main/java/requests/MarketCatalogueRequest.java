package requests;

import com.google.gson.JsonElement;
import entities.MarketCatalogue;
import entities.MarketFilter;
import enums.ApiNgOperation;
import enums.MarketProjection;
import enums.MarketSort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Client;
import utils.JsonConverter;

import java.util.*;

/**
 * Created by Evgeniy Slobozheniuk on 11.01.18.
 */
public class MarketCatalogueRequest {
    private static final Logger log = LogManager.getLogger(MarketCatalogueRequest.class);

    private Client client;
    private Map<String, Object> params;

    public MarketCatalogueRequest(final Client client) {
        final Set<MarketProjection> marketProjection = new HashSet<>();
        marketProjection.add(MarketProjection.EVENT);
        marketProjection.add(MarketProjection.RUNNER_DESCRIPTION);
        marketProjection.add(MarketProjection.MARKET_START_TIME);
        marketProjection.add(MarketProjection.MARKET_DESCRIPTION);
        this.params = new HashMap<String, Object>() {{
            put("marketProjection", marketProjection);
            put("sort", MarketSort.FIRST_TO_START);
            put("maxResults", 1000);
        }};
        this.client = client;
    }

    public List<MarketCatalogue> getObjects(MarketFilter marketFilter) {
        this.params.put("filter", marketFilter);
        List<MarketCatalogue> marketCatalogueList = new ArrayList<>();
        //Using the 'get()' method extract 'result' array and parse it as a JsonArray
        JsonElement jsonElement = JsonConverter.convertFromJson(getJSONString()).get("result");
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            marketCatalogueList.add(JsonConverter.convertFromJson(element.toString(), MarketCatalogue.class));
        }
        return marketCatalogueList;
    }

    public String getJSONString() {
        String json = client.execute(ApiNgOperation.LISTMARKETCATALOGUE.getOperationName(), params);
        return json;
    }
}

package requests;

import com.google.gson.JsonElement;
import entities.MarketBook;
import entities.PriceProjection;
import enums.PriceData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import enums.ApiNgOperation;
import utils.Client;
import utils.JsonConverter;

import java.util.*;

/**
 * Created by Evgeniy Slobozheniuk on 28.02.2018.
 */
public class MarketBookRequest {
    private static final Logger log = LogManager.getLogger(MarketBookRequest.class);

    private Client client;
    private Map<String, Object> params;

    public MarketBookRequest(final Client client) {
        final PriceProjection priceProjection = new PriceProjection();
        final Set<PriceData> priceDataSet = new HashSet<PriceData>();
        priceDataSet.add(PriceData.EX_ALL_OFFERS);
        priceProjection.setPriceData(priceDataSet);
        this.params = new HashMap<String, Object>() {{
            put("priceProjection", priceProjection);
        }};
        this.client = client;
    }

    public List<MarketBook> getObjects(final List<String> marketIds) {
        this.params.put("marketIds", marketIds);
        List<MarketBook> marketBookList = new ArrayList<>();
        //Using the 'get()' method extract 'result' array and parse it as a JsonArray
        JsonElement jsonElement = JsonConverter.convertFromJson(getJSONString()).get("result");
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            marketBookList.add(JsonConverter.convertFromJson(element.toString(), MarketBook.class));
        }
        return marketBookList;
    }

    private String getJSONString() {
        String json = client.execute(ApiNgOperation.LISTMARKETBOOK.getOperationName(), params);
        return json;
    }
}

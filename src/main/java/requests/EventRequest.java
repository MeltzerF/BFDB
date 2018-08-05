package requests;

import com.google.gson.JsonElement;
import entities.Event;
import entities.EventType;
import entities.MarketCatalogue;
import entities.MarketFilter;
import enums.ApiNgOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Client;
import utils.JsonConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeniy Slobozheniuk on 29.11.17.
 */
public class EventRequest {
    private static final Logger log = LogManager.getLogger(EventTypeRequest.class);

    private Client client;

    public EventRequest(final Client client) {
        this.client = client;
    }

    public List<Event> getObjects(final MarketFilter marketFilter) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("filter", marketFilter);
        }};
        List<Event> eventTypeList = new ArrayList<>();
        //Using the 'get()' method extract 'result' array and parse it as a JsonArray
        JsonElement jsonElement = JsonConverter.convertFromJson(getJSONString(params)).get("result");
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            eventTypeList.add(JsonConverter.convertFromJson(JsonConverter.convertFromJson(element.toString()).get("event").toString(), Event.class));
        }
        return eventTypeList;
    }

    public String getJSONString(final Map<String, Object> params) {
        String result = client.execute(ApiNgOperation.LISTEVENTS.getOperationName(), params);
        return result;
    }
}

package requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import entities.Event;
import entities.EventType;
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
 * Created by Evgeniy Slobozheniuk on 21.11.17.
 */
public class EventTypeRequest {
    private static final Logger log = LogManager.getLogger(EventTypeRequest.class);

    private Client client;

    public EventTypeRequest(final Client client) {
        this.client = client;
    }

    //TODO: Refactor method to look like List<T> parseObjects(String json)
    //TODO: Why pass filter in the method when it can be passed in constructor and called from method
    public List<EventType> getObjects(final MarketFilter filter) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("filter", filter);
        }};
        List<EventType> eventTypeList = new ArrayList<>();
        //Using the 'get()' method extract 'result' array and parse it as a JsonArray
        JsonElement jsonElement = JsonConverter.convertFromJson(getJSONString(params)).get("result");
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            eventTypeList.add(JsonConverter.convertFromJson(JsonConverter.convertFromJson(element.toString()).get("eventType").toString(), EventType.class));
        }
        return eventTypeList;
    }

    public String getJSONString(final Map<String, Object> params) {
        String result = client.execute(ApiNgOperation.LISTEVENTTYPES.getOperationName(), params);
        return result;
    }
}

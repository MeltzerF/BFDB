package utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by Evgeniy Slobozheniuk on 20.11.17.
 */
public class JsonConverter {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new ISO8601DateTypeAdapter()).create();

    public static <T> T convertFromJson(String toConvert, Class<T> clazz){
        return gson.fromJson(toConvert, clazz);
    }

    public static <T> T convertFromJson(String toConvert, Type typeOfT) {
        return gson.fromJson(toConvert, typeOfT);
    }

    public static JsonObject convertFromJson(String toConvert) { return gson.fromJson(toConvert, JsonObject.class); }

    public static <T> T convertFromJson(JsonElement element, Type typeOfT) { return gson.fromJson(element, typeOfT); }

    public static String convertToJson(Object toConvert){
        return gson.toJson(toConvert);
    }
}

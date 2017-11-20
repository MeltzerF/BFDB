package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public static String convertToJson(Object toConvert){
        return gson.toJson(toConvert);
    }
}

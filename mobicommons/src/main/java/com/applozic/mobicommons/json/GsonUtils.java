package com.applozic.mobicommons.json;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by devashish on 22/12/14.
 */
public class GsonUtils {

    private static final String TAG = "GsonUtils";

    public static String getJsonWithExposeFromObject(Object object, Type type) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(object, type);
    }

    public static String getJsonFromObject(Object object, Type type) {
        Gson gson = new Gson();
        return gson.toJson(object, type);
    }

    public static Object getObjectFromJson(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

}

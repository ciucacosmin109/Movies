package com.marius.movies.fetchers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class DataParserJson {
    // General parsing methods with validations
    public static String getString(JSONObject json, String property) throws JSONException {
        if (json.has(property))
            return json.getString(property);
        else
            return null;
    }
    public static int getInt(JSONObject json, String property) throws JSONException {
        if (json.has(property))
            return json.getInt(property);
        else
            return 0;
    }
    public static float getFloat(JSONObject json, String property) throws JSONException {
        if (json.has(property))
            return (float) json.getDouble(property);
        else
            return 0.0f;
    }
    public static boolean getBoolean(JSONObject json, String property) throws JSONException {
        if (json.has(property))
            return json.getBoolean(property);
        else
            return false;
    }
    public static JSONArray getJsonArray(JSONObject json, String property) throws JSONException {
        if (json.has(property))
            return json.getJSONArray(property);
        else
            return null;
    }

}

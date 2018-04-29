//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GsonUtil {
    public static Gson gson = new Gson();

    public GsonUtil() {
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T toObject(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }

    public static <T> String toJson(T object) {
        return gson.toJson(object);
    }

    public static JSONObject getJsonObject(JSONArray jsonArray, int index) {
        try {
            return jsonArray != null && index >= 0 && index < jsonArray.length()?jsonArray.getJSONObject(index):null;
        } catch (JSONException var3) {
            return null;
        }
    }

    public static JSONArray getJsonArray(JSONObject jsonObject, String key) {
        try {
            if(jsonObject != null && jsonObject.has(key)) {
                return jsonObject.getJSONArray(key);
            }
        } catch (Exception var3) {
            return new JSONArray();
        }

        return new JSONArray();
    }

    public static String getJsonStringValue(JSONObject jsonObject, String key) {
        return getJsonStringValue(jsonObject, key, "");
    }

    public static String getJsonStringValue(JSONObject jsonObject, String key, String defaultValue) {
        try {
            if(jsonObject != null && jsonObject.has(key)) {
                String e = jsonObject.getString(key).trim();
                if(e.equals("null")) {
                    e = "";
                }

                return e;
            } else {
                return defaultValue;
            }
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public static int getJsonIntegerValue(JSONObject json, String key) {
        return getJsonIntegerValue(json, key, 0);
    }

    public static int getJsonIntegerValue(JSONObject jsonObject, String key, int defaultValue) {
        try {
            return jsonObject != null && jsonObject.has(key)?jsonObject.getInt(key):defaultValue;
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public static Long getJsonLongValue(JSONObject json, String key) {
        return getJsonLongValue(json, key, Long.valueOf(0L));
    }

    public static Long getJsonLongValue(JSONObject jsonObject, String key, Long defaultValue) {
        try {
            return jsonObject != null && jsonObject.has(key)?Long.valueOf(jsonObject.getLong(key)):defaultValue;
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public static float getJsonFloatValue(JSONObject jsonObject, String key, float defaultValue) {
        try {
            return jsonObject != null && jsonObject.has(key)?Float.valueOf(jsonObject.getString(key)).floatValue():defaultValue;
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public static boolean getJsonBooleanValue(JSONObject jsonObject, String key, boolean defaultValue) {
        try {
            return jsonObject != null && jsonObject.has(key)?jsonObject.getBoolean(key):defaultValue;
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public static JSONObject getJsonObject(JSONObject jsonObject, String key) {
        try {
            if(jsonObject != null && jsonObject.has(key)) {
                return jsonObject.getJSONObject(key);
            }
        } catch (Exception var3) {
            return new JSONObject();
        }

        return new JSONObject();
    }
}

package ru.hse.germandilio.jsonparser.parsing.model;

import ru.hse.germandilio.jsonparser.utils.JsonDisplayUtils;
import ru.hse.germandilio.jsonparser.exceptions.JSONTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonObject {
    private final Map<String, Object> dictionary = new HashMap<>();

    public Object get(String key) {
        return dictionary.get(key);
    }

    public boolean containsKey(String key) {
        return dictionary.containsKey(key);
    }

    public void put(String key, Object value) {
        dictionary.put(key, value);
    }

    public List<Map.Entry<String, Object>> getAllEntries() {
        return new ArrayList<>(dictionary.entrySet());
    }

    public JsonObject getJsonObject(String key) {
        if (!dictionary.containsKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        if (!(dictionary.get(key) instanceof JsonObject)) {
            throw new JSONTypeException("Type of object by key: " + key + ", isn't JsonObject");
        }
        return (JsonObject) dictionary.get(key);
    }

    public JsonArray getJsonArray(String key) {
        if (!dictionary.containsKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        if (!(dictionary.get(key) instanceof JsonArray)) {
            throw new JSONTypeException("Type of object by key: " + key + ", isn't JsonArray");
        }
        return (JsonArray) dictionary.get(key);
    }

    @Override
    public String toString() {
        return JsonDisplayUtils.convertToString(this);
    }
}

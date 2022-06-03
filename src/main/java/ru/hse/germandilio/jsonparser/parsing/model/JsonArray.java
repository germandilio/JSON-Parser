package ru.hse.germandilio.jsonparser.parsing.model;

import ru.hse.germandilio.jsonparser.exceptions.JSONTypeException;
import ru.hse.germandilio.jsonparser.utils.JsonDisplayUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonArray implements Iterable<Object> {
    private final List<Object> array = new ArrayList<>();

    public void add(Object object) {
        array.add(object);
    }

    public Object get(int index) {
        return array.get(index);
    }

    public int size() {
        return array.size();
    }

    public JsonObject getJsonObject(int index) {
        Object ref = array.get(index);
        if (!(ref instanceof JsonObject)) {
            throw new JSONTypeException("Type of object by index: " + index + ", isn't JsonObject");
        }
        return (JsonObject) ref;
    }

    public JsonArray getJsonArray(int index) {
        Object ref = array.get(index);
        if (!(ref instanceof JsonArray)) {
            throw new JSONTypeException("Type of object by index: " + index + ", isn't JsonArray");
        }
        return (JsonArray) ref;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Object> iterator() {
        return array.listIterator();
    }

    @Override
    public String toString() {
        return JsonDisplayUtils.convertToString(this);
    }
}

package ru.hse.germandilio.jsonparser.utils;

import ru.hse.germandilio.jsonparser.parsing.model.JsonArray;
import ru.hse.germandilio.jsonparser.parsing.model.JsonObject;

import java.util.Map;

public class JsonDisplayUtils {
    private static final String NESTING_INDENT = "    ";

    private static final char OBJECT_START = '{';
    private static final char OBJECT_END = '}';
    private static final char ARRAY_START = '[';
    private static final char ARRAY_END = ']';

    private static int callDepth = 0;

    public static String convertToString(JsonObject object) {
        StringBuilder sb = new StringBuilder();
        startObject(sb, OBJECT_START);

        var pairs = object.getAllEntries();
        for (int i = 0; i < pairs.size(); i++) {
            var pair = pairs.get(i);
            printKey(sb, pair);

            var value = pair.getValue();
            if (value instanceof JsonObject) {
                sb.append('\n');
                sb.append(convertToString(object.getJsonObject(pair.getKey())));
            } else if (value instanceof JsonArray) {
                sb.append('\n');
                sb.append(convertToString(object.getJsonArray(pair.getKey())));
            } else if (value instanceof String) {
                sb.append('"');
                sb.append(value);
                sb.append('"');
            } else {
                // number, null, boolean
                sb.append(value);
            }

            if (i < pairs.size() - 1) {
                sb.append(',');
            }
        }

        endObject(sb, OBJECT_END);
        return sb.toString();
    }

    public static String convertToString(JsonArray array) {
        StringBuilder sb = new StringBuilder();
        startObject(sb, ARRAY_START);

        for (int i = 0; i < array.size(); i++) {
            var object = array.get(i);
            sb.append('\n');

            if (object instanceof JsonObject) {
                sb.append(convertToString(array.getJsonObject(i)));
            } else if (object instanceof JsonArray) {
                sb.append(convertToString(array.getJsonArray(i)));
            } else if (object instanceof String) {
                shift(sb);
                sb.append('"');
                sb.append(object);
                sb.append('"');
            } else {
                // number, null, boolean
                shift(sb);
                sb.append(object);
            }

            if (i < array.size() - 1) {
                sb.append(',');
            }
        }

        endObject(sb, ARRAY_END);
        return sb.toString();
    }

    private static void printKey(StringBuilder sb, Map.Entry<String, Object> pair) {
        sb.append('\n');
        shift(sb);
        sb.append('"');
        sb.append(pair.getKey());
        sb.append('"');
        sb.append(':');
        sb.append(' ');
    }

    private static void shift(StringBuilder sb) {
        sb.append(NESTING_INDENT.repeat(Math.max(0, callDepth)));
    }

    private static void startObject(StringBuilder sb, char symbol) {
        shift(sb);
        sb.append(symbol);
        ++callDepth;
    }

    private static void endObject(StringBuilder sb, char symbol) {
        sb.append('\n');
        --callDepth;
        shift(sb);
        sb.append(symbol);
    }
}

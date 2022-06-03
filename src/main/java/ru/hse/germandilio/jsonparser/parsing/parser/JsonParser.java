package ru.hse.germandilio.jsonparser.parsing.parser;

import ru.hse.germandilio.jsonparser.parsing.model.JsonObject;
import ru.hse.germandilio.jsonparser.utils.JsonDisplayUtils;
import ru.hse.germandilio.jsonparser.parsing.model.JsonArray;

import java.io.IOException;
import java.io.StringReader;

public class JsonParser {
    /**
     * Parse json to {@code JsonArray} or {@code JsonObject} type.
     * @param jsonString {@code String} represented .json file.
     * @return {@code Object}.
     * @throws IOException I/O exception.
     */
    public static Object fromJSON(String jsonString) throws IOException {
        var readerWrapper = new ReaderWrapper(new StringReader(jsonString));

        // first step - convert to sequence of tokens
        var lexicalAnalyzer = new LexicalAnalyzer(readerWrapper);
        var tokens = lexicalAnalyzer.convertToTokens();

        // second step convert sequence to JSON object
        var grammarAnalyzer = new GrammarAnalizer();
        return grammarAnalyzer.parse(tokens);
    }

    /**
     * Converts {@code JsonArray} to {@code String} representation in Json notation.
     * @param array {@code JsonArray} object to convert.
     * @return {@code String}
     */
    public static String toJson(JsonArray array) {
        return JsonDisplayUtils.convertToString(array);
    }

    /**
     * Converts {@code JsonArray} to {@code String} representation in Json notation.
     * @param object {@code JsonObject} object to convert.
     * @return {@code String}
     */
    public static String toJson(JsonObject object) {
        return JsonDisplayUtils.convertToString(object);
    }
}

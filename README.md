# JSON Parser #

Used for converting .json files to JsonObject or JsonArray.

## API ##
- Parse json to JsonArray or JsonObject type. 
```java
public class JsonParser {
    ...
    /**
     * Parse json to {@code JsonArray} or {@code JsonObject} type.
     * @param jsonString {@code String} represented .json file.
     * @return {@code Object}.
     * @throws IOException I/O exception.
     */
    public static Object fromJSON(String jsonString) throws IOException {
    }
    ...
}
```
- Convert from JsonObject ot JsonArray to string.
```java
public class JsonParser {
    ...
    /**
     * Converts {@code JsonArray} to {@code String} representation in Json notation.
     * @param array {@code JsonArray} object to convert.
     * @return {@code String}
     */
    public static String toJson(JsonArray array) {
    }

    /**
     * Converts {@code JsonArray} to {@code String} representation in Json notation.
     * @param object {@code JsonObject} object to convert.
     * @return {@code String}
     */
    public static String toJson(JsonObject object) {
    }
    ...
}
```

Development timing: ~ 25-27 October 2022
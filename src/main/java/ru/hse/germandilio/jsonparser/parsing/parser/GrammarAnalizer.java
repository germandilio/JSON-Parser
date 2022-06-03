package ru.hse.germandilio.jsonparser.parsing.parser;

import ru.hse.germandilio.jsonparser.exceptions.JSONParseException;
import ru.hse.germandilio.jsonparser.parsing.model.JsonArray;
import ru.hse.germandilio.jsonparser.parsing.model.JsonObject;
import ru.hse.germandilio.jsonparser.parsing.token.Token;
import ru.hse.germandilio.jsonparser.parsing.token.TokenSequence;
import ru.hse.germandilio.jsonparser.parsing.token.TokenType;

public class GrammarAnalizer {
    /**
     * expected tokens: SEPARATOR_COMMA(32) | END_OBJECT(2).
     * For string, null, boolean and number.
     */
    private static final int IN_FINISHED_VALUE = 34;

    /**
     * expected tokens: STRING(256) | END_OBJECT(2).
     * Initialization of object after START_OBJECT token.
     */
    private static final int INIT_OBJECT = 258;

    /**
     * expected tokens: SEPARATOR_COLON(16).
     * Used after initialization of key.
     */
    private static final int END_OF_KEY = 16;

    /**
     * expected tokens: BOOLEAN(512) | STRING(256) | NUMBER(128) | NULL(64) | START_OBJECT(1) | START_ARRAY(4).
     * Used for value after key and in array after comma.
     */
    private static final int IN_VALUE = 965;

    /**
     * expected tokens: STRING(256).
     * Used after initialization of key-value pair.
     */
    private static final int NEXT_KEY_VALUE_PAIR = 256;

    /**
     * expected tokens: START_ARRAY(4) | END_ARRAY(8) | START_OBJECT(1) | NULL(64) | NUMBER(128) | BOOLEAN(512) | STRING(256).
     * For start of array.
     */
    private static final int START_ARRAY = 973;

    /**
     * expected tokens: SEPARATOR_COMMA(32) | END_ARRAY(8).
     * Used for combinations in array sequence of tokens.
     */
    private static final int IN_ARRAY = 40;

    private TokenSequence tokens;

    public Object parse(TokenSequence tokens) {
        tokens.reset();
        this.tokens = tokens;
        return analizeJSONGrammar();
    }

    private Object analizeJSONGrammar() {
        var token = tokens.next();
        if (token == null) {
            return new JsonObject();
        }

        if (token.getType() == TokenType.START_ARRAY) {
            return parseJsonArray();
        }
        if (token.getType() == TokenType.START_OBJECT) {
            return parseJsonObject();
        }

        throw new JSONParseException("Invalid token sequence. Should start file only from tokens:\"{\" or \"[\"");
    }

    private void checkWithException(Token token, int mask) {
        if (!token.isValidToken(mask)) {
            throw new JSONParseException("Invalid token:" + token);
        }
    }

    private JsonObject parseJsonObject() {
        JsonObject jsonObject = new JsonObject();
        String key = null;
        int expectedToken = INIT_OBJECT;

        while (tokens.hasMore()) {
            Token token = tokens.next();
            switch (token.getType()) {
                case START_OBJECT -> {
                    checkWithException(token, expectedToken);
                    // recursion processing of inner object
                    jsonObject.put(key, parseJsonObject());
                    expectedToken = IN_FINISHED_VALUE;
                }
                case NUMBER -> {
                    checkWithException(token, expectedToken);
                    jsonObject.put(key, defineNumberType(token.getValue()));
                    expectedToken = IN_FINISHED_VALUE;
                }
                case NULL -> {
                    checkWithException(token, expectedToken);
                    jsonObject.put(key, null);
                    expectedToken = IN_FINISHED_VALUE;
                }
                case BOOLEAN -> {
                    checkWithException(token, expectedToken);
                    jsonObject.put(key, Boolean.valueOf(token.getValue()));
                    expectedToken = IN_FINISHED_VALUE;
                }
                case STRING -> {
                    checkWithException(token, expectedToken);
                    TokenType previousTokenType = tokens.previousTokenType();
                    if (previousTokenType == TokenType.SEPARATOR_COLON) {
                        jsonObject.put(key, token.getValue());
                        expectedToken = IN_FINISHED_VALUE;
                    } else {
                        key = token.getValue();
                        expectedToken = END_OF_KEY;
                    }
                }
                case SEPARATOR_COLON -> {
                    checkWithException(token, expectedToken);
                    expectedToken = IN_VALUE;
                }
                case SEPARATOR_COMMA -> {
                    checkWithException(token, expectedToken);
                    expectedToken = NEXT_KEY_VALUE_PAIR;
                }
                case START_ARRAY -> {
                    checkWithException(token, expectedToken);
                    // processing of array
                    jsonObject.put(key, parseJsonArray());
                    expectedToken = IN_FINISHED_VALUE;
                }
                case END_OBJECT, END_DOCUMENT -> {
                    checkWithException(token, expectedToken);
                    return jsonObject;
                }
                default -> throw new JSONParseException("Unexpected token (was provided invalid type by JSON rules).");
            }
        }
        // if there was no END_DOCUMENT or END_OBJECT provided.
        throw new JSONParseException("Invalid sequence of tokens");
    }

    private JsonArray parseJsonArray() {
        JsonArray jsonArray = new JsonArray();
        int expectToken = START_ARRAY;

        while (tokens.hasMore()) {
            Token token = tokens.next();
            switch (token.getType()) {
                case START_OBJECT -> {
                    checkWithException(token, expectToken);
                    jsonArray.add(parseJsonObject());
                    expectToken = IN_ARRAY;
                }
                case START_ARRAY -> {
                    checkWithException(token, expectToken);
                    jsonArray.add(parseJsonArray());
                    expectToken = IN_ARRAY;
                }
                case NULL -> {
                    checkWithException(token, expectToken);
                    jsonArray.add(null);
                    expectToken = IN_ARRAY;
                }
                case NUMBER -> {
                    checkWithException(token, expectToken);
                    jsonArray.add(defineNumberType(token.getValue()));
                    expectToken = IN_ARRAY;
                }
                case BOOLEAN -> {
                    checkWithException(token, expectToken);
                    jsonArray.add(Boolean.valueOf(token.getValue()));
                    expectToken = IN_ARRAY;
                }
                case STRING -> {
                    checkWithException(token, expectToken);
                    jsonArray.add(token.getValue());
                    expectToken = IN_ARRAY;
                }
                case SEPARATOR_COMMA -> {
                    checkWithException(token, expectToken);
                    expectToken = IN_VALUE;
                }
                case END_ARRAY, END_DOCUMENT -> {
                    checkWithException(token, expectToken);
                    return jsonArray;
                }
                default -> throw new JSONParseException("Unexpected token (was provided invalid type by JSON rules).");
            }
        }
        // if there was no END_DOCUMENT or END_OBJECT provided.
        throw new JSONParseException("Invalid sequence of tokens");
    }

    private Object defineNumberType(String tokenValue) {
        // define 3 types: double, long and int.
        if (tokenValue.contains("e") || tokenValue.contains("E") || tokenValue.contains(".")) {
            return Double.valueOf(tokenValue);
        } else {
            var number = Long.valueOf(tokenValue);
            if (number < Integer.MAX_VALUE && number > Integer.MIN_VALUE) {
                return number.intValue();
            } else {
                return number;
            }
        }
    }
}

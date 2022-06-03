package ru.hse.germandilio.jsonparser.parsing.parser;

import ru.hse.germandilio.jsonparser.exceptions.JSONParseException;
import ru.hse.germandilio.jsonparser.parsing.token.Token;
import ru.hse.germandilio.jsonparser.parsing.token.TokenSequence;
import ru.hse.germandilio.jsonparser.parsing.token.TokenType;

import java.io.IOException;
import java.util.Objects;

public class LexicalAnalyzer {
    private static class SpecialKeywords {
        private static final String TRUE = "true";
        private static final String FALSE = "false";
        private static final String NULL = "null";
    }

    private final ReaderWrapper reader;
    private final TokenSequence tokens;

    public LexicalAnalyzer(ReaderWrapper reader) {
        this.reader = reader;
        tokens = new TokenSequence();
    }

    /**
     * Convert stream from {@code reader} to sequence of tokens.
     *
     * @return {@code TokenSequence} - list of tokens in direct order
     * @throws IOException if stream has invalid symbols / located in wrong order, or cannot update buffer when read from {@code reader}
     */
    public TokenSequence convertToTokens() throws IOException {
        Token token = new Token(TokenType.START_DOCUMENT, null);

        while (token.getType() != TokenType.END_DOCUMENT) {
            token = getToken();
            tokens.add(token);
        }
        return tokens;
    }

    private Token getToken() throws IOException {
        while (!reader.isEmpty()) {
            char currentCharacter = (char) reader.next();

            if (!isWhiteSpace(currentCharacter)) {
                return recognizeToken(currentCharacter);
            }
        }
        return new Token(TokenType.END_DOCUMENT, null);
    }

    private Token recognizeToken(char character) throws IOException {
        switch (character) {
            case '{':
                return new Token(TokenType.START_OBJECT, character);
            case '}':
                return new Token(TokenType.END_OBJECT, character);
            case ':':
                return new Token(TokenType.SEPARATOR_COLON, character);
            case ',':
                return new Token(TokenType.SEPARATOR_COMMA, character);
            case '[':
                return new Token(TokenType.START_ARRAY, character);
            case ']':
                return new Token(TokenType.END_ARRAY, character);
            case 'n':
                return readNullToken();
            case 't':
            case 'f':
                return readBooleanToken();
            case '"':
                return readStringToken();
            default:
                if (isDigit(character)) {
                    return readNumberToken();
                }

                throw new JSONParseException("Cannot recognize symbol:" + character);
        }
    }

    private boolean isDigit(char character) {
        String mask = "[0-9]|[+.-]|[eE]";
        return String.valueOf(character).matches(mask);
    }

    private boolean isWhiteSpace(char character) {
        return character == ' ' || character == '\n' || character == '\r' || character == '\t';
    }

    private Token readNullToken() throws IOException {
        String result = readKeyword(SpecialKeywords.NULL);
        if (!Objects.equals(result, SpecialKeywords.NULL)) {
            throw new JSONParseException("Invalid token. Expected \"" + SpecialKeywords.NULL + "\", but was provided" + result.toString());
        }
        return new Token(TokenType.NULL, result);
    }

    private Token readBooleanToken() throws IOException {
        String result = null;
        if (reader.peek() == 't') {
            result = readKeyword(SpecialKeywords.TRUE);
        } else if (reader.peek() == 'f') {
            result = readKeyword(SpecialKeywords.FALSE);
        }

        if (!Objects.equals(result, SpecialKeywords.TRUE) && !Objects.equals(result, SpecialKeywords.FALSE)) {
            throw new JSONParseException("Invalid token. Expected \"" + SpecialKeywords.TRUE + "\" or \"" + SpecialKeywords.FALSE + "\", but was provided" + result);
        }
        return new Token(TokenType.BOOLEAN, result);
    }

    private Token readStringToken() throws IOException {
        StringBuilder result = new StringBuilder();
        // read up to "
        int character = reader.next();

        while (character != '"') {
            if (character == ReaderWrapper.TERMINATION_SYMBOL) {
                throw new IOException("Cannot read next character");
            }
            if (character == '\n' || character == '\r') {
                throw new JSONParseException("Invalid character");
            }

            if (character == '\\') {
                // escape sequence expected
                if (!isEscapeSequence()) {
                    throw new JSONParseException("Invalid escape sequence");
                }
                result.append((char) character);
                character = reader.peek();
            }

            result.append((char) character);
            character = reader.next();
        }

        return new Token(TokenType.STRING, result.toString());
    }

    private Token readNumberToken() throws IOException {
        StringBuilder sb = new StringBuilder();
        int character = reader.peek();
        while (isDigit((char) character)) {
            sb.append((char) character);
            character = reader.next();

            if (character == ReaderWrapper.TERMINATION_SYMBOL) {
                throw new IOException("End of stream reached");
            }
        }
        // back pos to the next symbol after number
        reader.back();
        String result = sb.toString();

        try {
            Double.valueOf(result);
        } catch (NumberFormatException cause) {
            throw new JSONParseException("Invalid number (sequence of characters). Provided: " + result, cause);
        }
        return new Token(TokenType.NUMBER, result);
    }

    private String readKeyword(String expected) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append((char) reader.peek());

        for (int i = 1; i < expected.length(); i++) {
            result.append((char) reader.next());
        }
        return result.toString();
    }

    private boolean isEscapeSequence() {
        int ch = reader.peek();
        return ch == '"' || ch == '\\' || ch == 'u' || ch == 'r'
                || ch == 'n' || ch == 'b' || ch == 't' || ch == 'f';
    }
}

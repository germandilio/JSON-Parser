package ru.hse.germandilio.jsonparser.parsing.token;

public class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(TokenType type, char value) {
        this.type = type;
        this.value = String.valueOf(value);
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean isValidToken(int mask) {
        return type.isValidTokenTypeByMask(mask);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}

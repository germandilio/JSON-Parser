package ru.hse.germandilio.jsonparser.parsing.token;

public enum TokenType {
    START_OBJECT(1),
    END_OBJECT(2),
    START_ARRAY(4),
    END_ARRAY(8),
    SEPARATOR_COLON(16),
    SEPARATOR_COMMA(32),
    NULL(64),
    NUMBER(128),
    STRING(256),
    BOOLEAN(512),
    START_DOCUMENT(1024),
    END_DOCUMENT(2048);

    private final int code;

    TokenType(int code) {
        this.code = code;
    }

    /**
     * Check if this token type is valid by provided mask.
     *
     * @param mask - sequence of bits (ex. 1000000010001111).
     * @return true - if it's matches to mask, otherwise false.
     */
    public boolean isValidTokenTypeByMask(int mask) {
        return (code & mask) != 0;
    }
}

package ru.hse.germandilio.jsonparser.exceptions;

public class SerializationException extends RuntimeException {
    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Exception cause) {
        super(message, cause);
    }
}

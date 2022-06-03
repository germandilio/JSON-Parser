package ru.hse.germandilio.jsonparser.parsing.parser;

import java.io.IOException;
import java.io.Reader;

public final class ReaderWrapper {
    public static final int TERMINATION_SYMBOL = -1;

    private static final int BUFFER_SIZE = 8192;

    private final Reader reader;
    private final char[] buffer;

    private int pos;
    private int currentSize;

    public ReaderWrapper(Reader reader) {
        this.reader = reader;
        buffer = new char[BUFFER_SIZE];
        pos = 0;
        currentSize = 0;
    }

    /**
     * Read next character.
     *
     * @return Single character, or -1 if the stream reached his end.
     * @throws IOException Cannot fill buffer from reader.
     */
    public int next() throws IOException {
        if (isEmpty()) {
            return TERMINATION_SYMBOL;
        }
        return buffer[pos++];
    }

    /**
     * @return Last char read, or -1 if the end of the stream is reached.
     */
    public int peek() {
        if (pos - 1 >= currentSize) {
            return TERMINATION_SYMBOL;
        }
        return buffer[Math.max(pos - 1, 0)];
    }

    /**
     * Return position on previous position
     */
    public void back() {
        pos = Math.max(--pos, 0);
    }

    /**
     * Check if reader is empty.
     *
     * @return True if empty, false otherwise
     * @throws IOException Cannot fill buffer from reader.
     */
    public boolean isEmpty() throws IOException {
        if (pos < currentSize) {
            return false;
        }

        updateBuffer();
        return pos >= currentSize;
    }

    private void updateBuffer() throws IOException {
        int responseLength = reader.read(buffer);
        if (responseLength == -1) {
            return;
        }

        pos = 0;
        currentSize = responseLength;
    }
}

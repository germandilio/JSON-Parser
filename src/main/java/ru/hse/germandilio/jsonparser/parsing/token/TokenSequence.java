package ru.hse.germandilio.jsonparser.parsing.token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TokenSequence implements Iterable<Token> {
    private final List<Token> tokens = new ArrayList<>();

    private int pos;

    public TokenSequence() {
        reset();
    }

    public void add(Token token) {
        tokens.add(token);
        pos++;
    }

    public void reset() {
        pos = 0;
    }

    public int size() {
        return tokens.size();
    }

    public boolean hasMore() {
        return pos < tokens.size();
    }

    public Token next() {
        if (pos >= tokens.size()) {
            return null;
        }
        return tokens.get(pos++);
    }

    public Token peek() {
        if (pos >= tokens.size()) {
            return null;
        }
        return tokens.get(pos);
    }

    public TokenType previousTokenType() {
        if (pos - 2 >= tokens.size() || pos - 2 < 0) {
            throw new IndexOutOfBoundsException("Previous token doesnt exist");
        }

        return tokens.get(pos - 2).getType();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Token> iterator() {
        return tokens.listIterator();
    }
}

package com.agonyforge.core.controller.interpret.delegate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class QuotingSplitterTest {
    @Test
    void testSplit() {
        String in = "one two three";
        String[] tokens = QuotingSplitter.split(in);

        assertArrayEquals(new String[] {"one", "two", "three"}, tokens);
    }

    @Test
    void testSplitCaps() {
        String in = "One tWo threE";
        String[] tokens = QuotingSplitter.split(in);

        assertArrayEquals(new String[] {"One", "tWo", "threE"}, tokens);
    }

    @Test
    void testSingleWord() {
        String in = "word";
        String[] tokens = QuotingSplitter.split(in);

        assertArrayEquals(new String[] {"word"}, tokens);
    }

    @Test
    void testDoubleQuote() {
        String in = "write \"You're dead!\" on the sign";
        String[] tokens = QuotingSplitter.split(in);

        assertArrayEquals(new String[] {"write", "You're dead!", "on", "the", "sign"}, tokens);
    }

    @Test
    void testSingleQuote() {
        String in = "cast 'untangle shoelace' Fred";
        String[] tokens = QuotingSplitter.split(in);

        assertArrayEquals(new String[] {"cast", "untangle shoelace", "Fred"}, tokens);
    }

    @Test
    void testUnterminatedQuote() {
        String in = "write \"You're dead!";
        String[] tokens = QuotingSplitter.split(in);

        assertArrayEquals(new String[] {"write", "You're dead!"}, tokens);
    }
}

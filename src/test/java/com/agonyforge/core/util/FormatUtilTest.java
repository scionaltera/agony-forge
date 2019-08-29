package com.agonyforge.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatUtilTest {
    @Test
    void testSingleWord() {
        assertEquals("", FormatUtil.removeFirstWord("one"));
    }

    @Test
    void testTwoWords() {
        assertEquals("two", FormatUtil.removeFirstWord("one two"));
    }

    @Test
    void testThreeWords() {
        assertEquals("two three", FormatUtil.removeFirstWord("one two three"));
    }

    @Test
    void testMultipleSpaces() {
        assertEquals("two three", FormatUtil.removeFirstWord("one  two three"));
    }
}

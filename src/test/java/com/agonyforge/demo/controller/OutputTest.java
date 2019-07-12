package com.agonyforge.demo.controller;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OutputTest {
    @Test
    public void testDefaultConstructor() {
        Output empty = new Output();

        assertEquals(Collections.emptyList(), empty.toList());
        assertEquals("", empty.toString());
    }

    @Test
    public void testPreloadConstructorVararg() {
        Output full = new Output("one", "two", "three");
        List<String> expected = Arrays.asList("one", "two", "three");

        assertEquals(expected, full.toList());
        assertEquals("one\ntwo\nthree", full.toString());
    }

    @Test
    public void testPreloadConstructorCollection() {
        Output full = new Output(Arrays.asList("one", "two", "three"));
        List<String> expected = Arrays.asList("one", "two", "three");

        assertEquals(expected, full.toList());
        assertEquals("one\ntwo\nthree", full.toString());
    }

    @Test
    public void testAppendOutputsConstructor() {
        Output output = new Output("One");
        Output append1 = new Output("Two");
        Output append2 = new Output("Three");

        Output result = new Output(output, append1, append2);

        assertEquals(Arrays.asList("One", "Two", "Three"), result.getOutput());
    }

    @Test
    public void testAppendChaining() {
        Output output = new Output();

        assertEquals("", output.toString());

        output
            .append("Output!")
            .append("Now!");

        assertEquals(Arrays.asList("Output!", "Now!"), output.getOutput());
        assertEquals("Output!\nNow!", output.toString());
    }

    @Test
    public void testAppendVararg() {
        Output output = new Output();

        assertEquals("", output.toString());

        output.append("Output!", "Now!");

        assertEquals(Arrays.asList("Output!", "Now!"), output.getOutput());
        assertEquals("Output!\nNow!", output.toString());
    }

    @Test
    public void testAppendCollection() {
        Output output = new Output();

        assertEquals("", output.toString());

        output
            .append(Collections.singletonList("Output!"))
            .append(Collections.singletonList("Now!"));

        assertEquals(Arrays.asList("Output!", "Now!"), output.getOutput());
        assertEquals("Output!\nNow!", output.toString());
    }

    @Test
    public void testSecret() {
        Output output = new Output().setSecret(true);

        assertTrue(output.getSecret());
    }

    @Test
    public void testAppendSecret() {
        Output output = new Output();
        Output secret = new Output().setSecret(true);

        output.append(secret);

        assertTrue(output.getSecret());
    }

    @Test
    public void testEquality() {
        Output one = new Output("Testing");
        Output two = new Output("Testing");

        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
    }
}

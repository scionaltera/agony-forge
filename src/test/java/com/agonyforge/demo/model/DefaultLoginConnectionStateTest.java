package com.agonyforge.demo.model;

import org.junit.Test;

import static com.agonyforge.demo.model.DefaultLoginConnectionState.DEFAULT;
import static org.junit.Assert.assertEquals;

public class DefaultLoginConnectionStateTest {
    @Test
    public void testIndex() {
        assertEquals(0, DEFAULT.getIndex());
    }

    @Test
    public void testConverter() {
        new DefaultLoginConnectionState.Converter();
    }
}

package com.agonyforge.core.model;

import com.agonyforge.core.controller.interpret.delegate.DefaultLoginConnectionState;
import org.junit.Test;

import static com.agonyforge.core.controller.interpret.delegate.DefaultLoginConnectionState.DEFAULT;
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

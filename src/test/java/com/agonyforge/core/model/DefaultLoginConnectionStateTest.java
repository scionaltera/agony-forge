package com.agonyforge.core.model;

import com.agonyforge.core.controller.interpret.delegate.DefaultLoginConnectionState;
import org.junit.jupiter.api.Test;

import static com.agonyforge.core.controller.interpret.delegate.DefaultLoginConnectionState.DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultLoginConnectionStateTest {
    @Test
    void testIndex() {
        assertEquals(0, DEFAULT.getIndex());
    }

    @Test
    void testConverter() {
        new DefaultLoginConnectionState.Converter();
    }
}

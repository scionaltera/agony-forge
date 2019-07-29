package com.agonyforge.core.model;

import com.agonyforge.core.controller.interpret.PrimaryConnectionState;
import org.junit.jupiter.api.Test;

import static com.agonyforge.core.controller.interpret.PrimaryConnectionState.LOGIN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PrimaryConnectionStateTest {
    @Test
    void testIndex() {
        assertEquals(0, LOGIN.getIndex());
    }

    @Test
    void testConverter() {
        new PrimaryConnectionState.Converter();
    }
}

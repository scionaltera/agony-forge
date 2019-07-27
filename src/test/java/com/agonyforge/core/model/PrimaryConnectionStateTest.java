package com.agonyforge.core.model;

import com.agonyforge.core.controller.interpret.PrimaryConnectionState;
import org.junit.Test;

import static com.agonyforge.core.controller.interpret.PrimaryConnectionState.LOGIN;
import static org.junit.Assert.assertEquals;

public class PrimaryConnectionStateTest {
    @Test
    public void testIndex() {
        assertEquals(0, LOGIN.getIndex());
    }

    @Test
    public void testConverter() {
        new PrimaryConnectionState.Converter();
    }
}

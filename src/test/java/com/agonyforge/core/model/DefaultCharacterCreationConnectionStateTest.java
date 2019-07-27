package com.agonyforge.core.model;

import org.junit.Test;

import static com.agonyforge.core.model.DefaultCharacterCreationConnectionState.DEFAULT;
import static org.junit.Assert.assertEquals;

public class DefaultCharacterCreationConnectionStateTest {
    @Test
    public void testIndex() {
        assertEquals(0, DEFAULT.getIndex());
    }

    @Test
    public void testProperty() {
        assertEquals("askGender", DEFAULT.toProperty());
    }

    @Test
    public void testConverter() {
        new DefaultCharacterCreationConnectionState.Converter();
    }
}

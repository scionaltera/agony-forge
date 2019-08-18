package com.agonyforge.core.model;

import com.agonyforge.core.controller.interpret.delegate.creation.DefaultCharacterCreationConnectionState;
import org.junit.jupiter.api.Test;

import static com.agonyforge.core.controller.interpret.delegate.creation.DefaultCharacterCreationConnectionState.DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultCharacterCreationConnectionStateTest {
    @Test
    void testIndex() {
        assertEquals(0, DEFAULT.getIndex());
    }

    @Test
    void testProperty() {
        assertEquals("askGender", DEFAULT.toProperty());
    }

    @Test
    void testConverter() {
        new DefaultCharacterCreationConnectionState.Converter();
    }
}

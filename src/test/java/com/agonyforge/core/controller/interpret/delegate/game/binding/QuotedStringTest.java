package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class QuotedStringTest {
    private Creature creature;

    private QuotedString binding;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        creature = new Creature();

        binding = new QuotedString();
    }

    @Test
    void testBind() {
        assertTrue(binding.bind(creature, "TOKEN"));
        assertEquals("TOKEN", binding.getToken());
    }

    @Test
    void testBindEmptyToken() {
        assertFalse(binding.bind(creature, ""));
    }
}

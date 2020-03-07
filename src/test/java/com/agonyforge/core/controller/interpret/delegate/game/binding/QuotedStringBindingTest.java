package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class QuotedStringBindingTest {
    private Creature creature;

    private QuotedStringBinding binding;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        creature = new Creature();

        binding = new QuotedStringBinding();
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

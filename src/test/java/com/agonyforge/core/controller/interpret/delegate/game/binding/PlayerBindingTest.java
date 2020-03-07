package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.repository.CreatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlayerBindingTest {
    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private Creature actor;

    @Mock
    private Creature target;

    private PlayerBinding binding;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(creatureRepository
            .findByNameAndConnectionIsNotNull(eq("Stan")))
            .thenReturn(Optional.of(target));

        binding = new PlayerBinding(creatureRepository);
    }

    @Test
    void testBind() {
        boolean result = binding.bind(actor, "Stan");

        assertTrue(result);
        assertEquals(target, binding.getPlayer());

        verify(creatureRepository).findByNameAndConnectionIsNotNull(eq("Stan"));
    }

    @Test
    void testBindFailure() {
        boolean result = binding.bind(actor, "Jerry");

        assertFalse(result);
        assertNull(binding.getPlayer());

        verify(creatureRepository).findByNameAndConnectionIsNotNull(eq("Jerry"));
    }
}

package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.VerbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class VerbBindingTest {
    @Mock
    private VerbRepository verbRepository;

    private Creature creature;
    private Verb verb;

    private VerbBinding binding;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        creature = new Creature();
        verb = new Verb();

        verb.setName("verb");

        when(verbRepository.findFirstByNameIgnoreCaseStartingWith(any(Sort.class), eq("verb"))).thenReturn(Optional.of(verb));

        binding = new VerbBinding(verbRepository);
    }

    @Test
    void testBind() {
        assertTrue(binding.bind(creature, "verb"));
        assertEquals(verb, binding.getVerb());
    }

    @Test
    void testBindFailure() {
        assertFalse(binding.bind(creature, "noun"));
        assertNull(binding.getVerb());
    }
}

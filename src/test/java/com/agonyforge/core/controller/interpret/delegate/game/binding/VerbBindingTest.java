package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Role;
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

        Role playerRole = new Role("PLAYER");

        creature = new Creature();
        verb = new Verb();

        creature.getRoles().add(playerRole);

        verb.setName("verb");
        verb.getRoles().add(playerRole);

        when(verbRepository.findFirstByNameIgnoreCaseStartingWith(any(Sort.class), eq("verb"))).thenReturn(Optional.of(verb));

        binding = new VerbBinding(verbRepository);
    }

    @Test
    void testBind() {
        assertTrue(binding.bind(creature, "verb"));
        assertEquals(verb, binding.getVerb());
    }

    @Test
    void testBindForSuper() {
        creature.getRoles().clear();
        creature.getRoles().add(new Role("SUPER"));

        assertTrue(binding.bind(creature, "verb"));
        assertEquals(verb, binding.getVerb());
    }

    @Test
    void testBindFailureNoSuchVerb() {
        assertFalse(binding.bind(creature, "noun"));
        assertNull(binding.getVerb());
    }

    @Test
    void testBindFailureNoMatchingRole() {
        creature.getRoles().clear();

        assertFalse(binding.bind(creature, "verb"));
        assertNull(binding.getVerb());
    }
}

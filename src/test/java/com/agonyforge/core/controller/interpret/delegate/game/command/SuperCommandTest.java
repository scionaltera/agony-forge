package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Role;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.model.repository.VerbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SuperCommandTest {
    @Mock
    private UserDetailsManager userDetailsManager;

    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private VerbRepository verbRepository;

    private Output output;
    private Creature creature;
    private User user;
    private Verb verb;

    private SuperCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        output = new Output();

        creature = new Creature();
        creature.setName("Scion");

        user = new User("super", "duper", Collections.singletonList(new SimpleGrantedAuthority("PLAYER")));

        verb = new Verb();
        verb.setName("super");
        verb.setBean("verbCommand");
        verb.getRoles().add(new Role("PLAYER"));

        when(verbRepository.findFirstByNameIgnoreCaseStartingWith(any(Sort.class), eq("super"))).thenReturn(Optional.of(verb));
        when(userDetailsManager.loadUserByUsername(eq("Scion"))).thenReturn(user);

        command = new SuperCommand(userDetailsManager, creatureRepository, verbRepository);
    }

    @Test
    void testSuperActive() {
        command.invoke(creature, output);

        verify(userDetailsManager).updateUser(any(UserDetails.class));
        verify(creatureRepository).save(any());
        verify(verbRepository).save(any());
    }

    @Test
    void testSuperInactive() {
        verb.getRoles().clear();

        command.invoke(creature, output);

        verifyNoMoreInteractions(userDetailsManager);
        verifyNoMoreInteractions(creatureRepository);
        verify(verbRepository, never()).save(any());
    }
}

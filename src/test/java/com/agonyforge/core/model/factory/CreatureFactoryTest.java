package com.agonyforge.core.model.factory;

import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureDefinition;
import com.agonyforge.core.model.Gender;
import com.agonyforge.core.model.Role;
import com.agonyforge.core.model.repository.ConnectionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.model.repository.RoleRepository;
import com.agonyforge.core.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.agonyforge.core.model.Gender.FEMALE;
import static com.agonyforge.core.model.Gender.MALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreatureFactoryTest {
    @Mock
    private CommService commService;

    @Mock
    private Interpreter primary;

    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserDetailsManager userDetailsManager;

    private CreatureFactory creatureFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(creatureRepository.save(any())).thenAnswer(invocation -> {
            Creature creature = invocation.getArgument(0);

            if (creature.getId() == null) {
                creature.setId(UUID.randomUUID());
            }

            return creature;
        });

        UserDetails user = mock(UserDetails.class);
        Role playerRole = new Role("PLAYER");

        when(user.getAuthorities()).thenAnswer(i -> Collections.singletonList(new SimpleGrantedAuthority("PLAYER")));
        when(roleRepository.findById(eq("PLAYER"))).thenReturn(Optional.of(playerRole));
        when(userDetailsManager.loadUserByUsername(anyString())).thenReturn(user);

        creatureFactory = new CreatureFactory(
            commService,
            creatureRepository,
            connectionRepository,
            roleRepository,
            userDetailsManager);
    }

    @Test
    void testBuildNewPlayer() {
        Connection connection = new Connection();
        CreatureDefinition definition = new CreatureDefinition();

        definition.setPlayer(true);
        definition.setName("Result");
        definition.setGender(FEMALE);

        when(creatureRepository.findByDefinition(eq(definition))).thenReturn(Stream.empty());

        Creature result = creatureFactory.build(definition, primary, connection);

        assertNotNull(result.getConnection());
        assertEquals(definition, result.getDefinition());
        assertEquals("Result", result.getName());
        assertEquals(FEMALE, result.getGender());
        assertTrue(result.getRoles().stream().anyMatch(role -> "PLAYER".equals(role.getName())));

        verify(creatureRepository).save(any());
    }

    @Test
    void testBuildExistingPlayer() {
        Connection connection = new Connection();
        CreatureDefinition definition = new CreatureDefinition();
        Creature creature = new Creature();

        definition.setPlayer(true);
        definition.setName("Bob");
        definition.setGender(Gender.MALE);

        creature.setDefinition(definition);
        creature.setConnection(connection);
        creature.setName(definition.getName());
        creature.setGender(definition.getGender());
        creature.getRoles().add(new Role("PLAYER"));
        creature.getRoles().add(new Role("BUILDER"));

        when(creatureRepository.findByDefinition(eq(definition))).thenReturn(Stream.of(creature));

        Creature result = creatureFactory.build(definition, primary, connection);

        assertEquals(connection, result.getConnection());
        assertEquals(definition, result.getDefinition());
        assertEquals("Bob", result.getName());
        assertEquals(MALE, result.getGender());
        assertTrue(result.getRoles().stream().anyMatch(role -> "PLAYER".equals(role.getName())));
        assertTrue(result.getRoles().stream().anyMatch(role -> "BUILDER".equals(role.getName())));

        verify(creatureRepository).save(any());
    }
}

package com.agonyforge.core.controller.interpret.delegate.creation;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureDefinition;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.factory.CreatureFactory;
import com.agonyforge.core.model.factory.ZoneFactory;
import com.agonyforge.core.model.repository.ConnectionRepository;
import com.agonyforge.core.model.repository.CreatureDefinitionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.model.repository.RoleRepository;
import com.agonyforge.core.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Collections;
import java.util.UUID;

import static com.agonyforge.core.controller.interpret.PrimaryConnectionState.IN_GAME;
import static com.agonyforge.core.controller.interpret.delegate.creation.DefaultCharacterCreationConnectionState.DEFAULT;
import static com.agonyforge.core.model.Gender.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultCharacterCreationInterpreterDelegateTest {
    @Mock
    private CommService commService;

    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private CreatureDefinitionRepository creatureDefinitionRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ZoneFactory zoneFactory;

    @Mock
    private UserDetailsManager userDetailsManager;

    @Mock
    private Interpreter primary;

    @Captor
    private ArgumentCaptor<Creature> creatureArgumentCaptor;

    @Captor
    private ArgumentCaptor<CreatureDefinition> creatureDefinitionArgumentCaptor;

    private DefaultCharacterCreationInterpreterDelegate delegate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        CreatureFactory creatureFactory = new CreatureFactory(
            commService,
            creatureRepository,
            connectionRepository,
            roleRepository,
            userDetailsManager);

        when(creatureRepository.save(any())).thenAnswer(invocation -> {
            Creature creature = invocation.getArgument(0);

            if (creature.getId() == null) {
                creature.setId(UUID.randomUUID());
            }

            return creature;
        });

        when(creatureDefinitionRepository.save(any())).thenAnswer(invocation -> {
            CreatureDefinition definition = invocation.getArgument(0);

            if (definition.getId() == null) {
                definition.setId(UUID.randomUUID());
            }

            return definition;
        });

        when(zoneFactory.getStartZone()).thenAnswer(invocation -> {
            Zone zone = new Zone();

            zone.setId(1L);

            for (int i = 0; i < 10; i++) {
                Room room = new Room();

                room.setId(UUID.randomUUID());
                room.setSequence(i);

                zone.getRooms().add(room);
            }

            return zone;
        });

        UserDetails user = mock(UserDetails.class);

        when(user.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetailsManager.loadUserByUsername(anyString())).thenReturn(user);

        delegate = new DefaultCharacterCreationInterpreterDelegate(
            creatureFactory,
            creatureRepository,
            creatureDefinitionRepository,
            zoneFactory,
            commService);
    }

    @Test
    void testInterpret() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("m");
        connection.setName("Scion");

        when(primary.interpret(any(), any(), anyBoolean())).thenReturn(new Output("LOOK"));
        when(primary.prompt(any())).thenAnswer(i -> delegate.prompt(primary, connection));

        Output result = delegate.interpret(primary, input, connection);

        verify(creatureDefinitionRepository, atLeastOnce()).save(creatureDefinitionArgumentCaptor.capture());
        verify(creatureRepository, times(2)).save(creatureArgumentCaptor.capture());

        assertNotNull(result);

        CreatureDefinition capturedDef = creatureDefinitionArgumentCaptor.getValue();

        assertTrue(capturedDef.getPlayer());

        Creature captured = creatureArgumentCaptor.getValue();

        assertEquals(connection, captured.getConnection());
        assertEquals(connection.getName(), captured.getName());
        assertNotNull(captured.getRoom());

        assertEquals(MALE, captured.getGender());
        assertEquals(IN_GAME, connection.getPrimaryState());
        assertEquals(DEFAULT.name(), connection.getSecondaryState());

        verify(commService).echoToWorld(any(), eq(primary), eq(captured));
    }

    @Test
    void testFemaleGender() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("f");
        connection.setName("Bethany");

        when(primary.interpret(any(), any(), anyBoolean())).thenReturn(new Output("LOOK"));
        when(primary.prompt(any())).thenAnswer(i -> delegate.prompt(primary, connection));

        Output result = delegate.interpret(primary, input, connection);

        assertNotNull(result);

        verify(creatureDefinitionRepository, atLeastOnce()).save(creatureDefinitionArgumentCaptor.capture());
        verify(creatureRepository, atLeastOnce()).save(creatureArgumentCaptor.capture());

        CreatureDefinition capturedDef = creatureDefinitionArgumentCaptor.getValue();
        Creature captured = creatureArgumentCaptor.getValue();

        assertEquals(FEMALE, capturedDef.getGender());
        assertEquals(FEMALE, captured.getGender());
    }

    @Test
    void testNeutralGender() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("n");
        connection.setName("Pat");

        when(primary.interpret(any(), any(), anyBoolean())).thenReturn(new Output("LOOK"));
        when(primary.prompt(any())).thenAnswer(i -> delegate.prompt(primary, connection));

        Output result = delegate.interpret(primary, input, connection);

        assertNotNull(result);

        verify(creatureDefinitionRepository, atLeastOnce()).save(creatureDefinitionArgumentCaptor.capture());
        verify(creatureRepository, atLeastOnce()).save(creatureArgumentCaptor.capture());

        CreatureDefinition capturedDef = creatureDefinitionArgumentCaptor.getValue();
        Creature captured = creatureArgumentCaptor.getValue();

        assertEquals(NEUTRAL, capturedDef.getGender());
        assertEquals(NEUTRAL, captured.getGender());
    }

    @Test
    void testInvalidGender() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("t");
        connection.setName("Invalid");

        when(primary.prompt(any())).thenAnswer(i -> delegate.prompt(primary, connection));

        Output result = delegate.interpret(primary, input, connection);

        assertNotNull(result);

        verify(creatureDefinitionRepository, never()).save(any());
        verify(creatureRepository, never()).save(any());
    }

    @Test
    void testPrompt() {
        Connection connection = new Connection();

        connection.setName("Scion");

        Output result = delegate.prompt(primary, connection);

        assertNotNull(result);
    }
}

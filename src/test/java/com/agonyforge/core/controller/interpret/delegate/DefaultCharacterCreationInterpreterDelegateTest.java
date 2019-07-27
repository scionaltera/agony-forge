package com.agonyforge.core.controller.interpret.delegate;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureDefinition;
import com.agonyforge.core.model.factory.CreatureFactory;
import com.agonyforge.core.model.repository.ConnectionRepository;
import com.agonyforge.core.model.repository.CreatureDefinitionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static com.agonyforge.core.model.Gender.MALE;
import static com.agonyforge.core.controller.interpret.PrimaryConnectionState.IN_GAME;
import static com.agonyforge.core.controller.interpret.delegate.DefaultCharacterCreationConnectionState.DEFAULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DefaultCharacterCreationInterpreterDelegateTest {
    @Mock
    private CommService commService;

    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private CreatureDefinitionRepository creatureDefinitionRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private Interpreter primary;

    @Captor
    private ArgumentCaptor<Creature> creatureArgumentCaptor;

    private DefaultCharacterCreationInterpreterDelegate delegate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        CreatureFactory creatureFactory = new CreatureFactory(commService, creatureRepository, connectionRepository);

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

        delegate = new DefaultCharacterCreationInterpreterDelegate(creatureFactory, creatureRepository, creatureDefinitionRepository);
    }

    @Test
    public void testInterpret() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("m");
        connection.setName("Scion");

        when(primary.prompt(any())).thenAnswer(i -> delegate.prompt(primary, connection));

        Output result = delegate.interpret(primary, input, connection);

        verify(creatureRepository, atLeastOnce()).save(creatureArgumentCaptor.capture());

        Creature captured = creatureArgumentCaptor.getValue();

        assertNotNull(result);
        assertEquals(connection, captured.getConnection());
        assertEquals(connection.getName(), captured.getName());
        assertEquals(MALE, captured.getGender());
        assertEquals(IN_GAME, connection.getPrimaryState());
        assertEquals(DEFAULT.name(), connection.getSecondaryState());
    }

    @Test
    public void testPrompt() {
        Connection connection = new Connection();

        connection.setName("Scion");

        Output result = delegate.prompt(primary, connection);

        assertNotNull(result);
    }
}

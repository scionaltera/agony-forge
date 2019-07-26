package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureFactory;
import com.agonyforge.core.repository.CreatureRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static com.agonyforge.core.model.PrimaryConnectionState.IN_GAME;
import static com.agonyforge.core.model.DefaultCharacterCreationConnectionState.DEFAULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DefaultCharacterCreationInterpreterDelegateTest {
    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private Interpreter primary;

    @Captor
    private ArgumentCaptor<Creature> creatureArgumentCaptor;

    private DefaultCharacterCreationInterpreterDelegate delegate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        CreatureFactory creatureFactory = new CreatureFactory(creatureRepository);

        when(creatureRepository.save(any())).thenAnswer(invocation -> {
            Creature creature = invocation.getArgument(0);

            if (creature.getId() == null) {
                creature.setId(UUID.randomUUID());
            }

            return creature;
        });

        delegate = new DefaultCharacterCreationInterpreterDelegate(creatureFactory, creatureRepository);
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

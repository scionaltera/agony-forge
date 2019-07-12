package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.repository.CreatureRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BaseInterpreterTest {
    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    private Interpreter interpreter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        interpreter = new EchoInterpreter(creatureRepository, simpMessagingTemplate);
    }

    @Test
    public void testEcho() {
        Creature creature = new Creature();
        Connection connection = new Connection();
        Output output = new Output("Hello");

        connection.setSessionUsername("username");
        creature.setConnection(connection);

        interpreter.echo(creature, output);

        verify(simpMessagingTemplate).convertAndSendToUser(eq("username"), eq("/queue/output"), eq(output));
        verifyZeroInteractions(creatureRepository);

        assertEquals("Hello\n[default]> ", output.toString());
    }

    @Test
    public void testEchoNoConnection() {
        Creature creature = new Creature();
        Output output = new Output("Hello");

        interpreter.echo(creature, output);

        verifyZeroInteractions(simpMessagingTemplate, creatureRepository);
    }

    @Test
    public void testEchoNoSessionUsername() {
        Creature creature = new Creature();
        Connection connection = new Connection();
        Output output = new Output("Hello");

        creature.setConnection(connection);

        interpreter.echo(creature, output);

        verifyZeroInteractions(simpMessagingTemplate, creatureRepository);
    }

    @Test
    public void testEchoToWorld() {
        Creature included = new Creature();
        Connection includedConnection = new Connection();
        Creature excluded = new Creature();
        Connection excludedConnection = new Connection();
        Output output = new Output("Hello");

        // otherwise equals() will return true
        included.setId(UUID.randomUUID());
        excluded.setId(UUID.randomUUID());

        includedConnection.setSessionUsername("included");
        excludedConnection.setSessionUsername("excluded");

        included.setConnection(includedConnection);
        excluded.setConnection(excludedConnection);

        when(creatureRepository.findByConnectionIsNotNull())
            .thenReturn(Stream.of(included, excluded));

        interpreter.echoToWorld(output, excluded);

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq("included"),
            eq("/queue/output"),
            eq(output.append("[default]> ")));
        verifyNoMoreInteractions(simpMessagingTemplate);

        assertEquals("Hello\n[default]> ", output.toString());
    }
}

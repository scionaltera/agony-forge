package com.agonyforge.core.service;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.repository.CreatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CommServiceTest {
    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private Interpreter interpreter;

    @Mock
    private CommService commService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(interpreter.prompt(any())).thenReturn(new Output("[default]> "));

        commService = new CommService(creatureRepository, simpMessagingTemplate);
    }

    @Test
    void testEcho() {
        Creature creature = new Creature();
        Connection connection = new Connection();
        Output output = new Output("Hello");

        connection.setSessionUsername("username");
        creature.setConnection(connection);

        commService.echo(creature, interpreter, output);

        verify(simpMessagingTemplate).convertAndSendToUser(eq("username"), eq("/queue/output"), eq(output));
        verifyNoMoreInteractions(creatureRepository);

        assertEquals("Hello\n[default]> ", output.toString());
    }

    @Test
    void testEchoNoConnection() {
        Creature creature = new Creature();
        Output output = new Output("Hello");

        commService.echo(creature, interpreter, output);

        verifyNoMoreInteractions(simpMessagingTemplate, creatureRepository);
    }

    @Test
    void testEchoNoSessionUsername() {
        Creature creature = new Creature();
        Connection connection = new Connection();
        Output output = new Output("Hello");

        creature.setConnection(connection);

        commService.echo(creature, interpreter, output);

        verifyNoMoreInteractions(simpMessagingTemplate, creatureRepository);
    }

    @Test
    void testEchoToWorld() {
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

        commService.echoToWorld(output, interpreter, excluded);

        verify(simpMessagingTemplate).convertAndSendToUser(
            eq("included"),
            eq("/queue/output"),
            eq(output.append("[default]> ")));
        verifyNoMoreInteractions(simpMessagingTemplate);

        assertEquals("Hello\n[default]> ", output.toString());
    }
}

package com.agonyforge.core.model;

import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.repository.ConnectionRepository;
import com.agonyforge.core.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class CreatureFactoryTest {
    @Mock
    private CommService commService;

    @Mock
    private Interpreter primary;

    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    private CreatureFactory creatureFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(creatureRepository.save(any())).thenAnswer(invocation -> {
            Creature creature = invocation.getArgument(0);

            if (creature.getId() == null) {
                creature.setId(UUID.randomUUID());
            }

            return creature;
        });

        creatureFactory = new CreatureFactory(commService, creatureRepository, connectionRepository);
    }

    @Test
    public void testBuildNewPlayer() {
        Connection connection = new Connection();
        CreatureDefinition definition = new CreatureDefinition();
        Creature creature = new Creature();

        definition.setPlayer(true);
        definition.setName("Result");
        definition.setGender(Gender.FEMALE);

        creature.setDefinition(definition);
        creature.setConnection(connection);
        creature.setName(definition.getName());
        creature.setGender(definition.getGender());

        when(creatureRepository.findByDefinition(eq(definition))).thenReturn(Stream.of(creature));

        Creature result = creatureFactory.build(definition, primary, connection);

        assertNotNull(result.getConnection());
        assertEquals("Result", result.getName());

        verify(creatureRepository).save(any());
    }

    @Test
    public void testBuildExistingPlayer() {
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

        when(creatureRepository.findByDefinition(eq(definition))).thenReturn(Stream.of(creature));

        Creature result = creatureFactory.build(definition, primary, connection);

        assertEquals(connection, result.getConnection());
        assertEquals("Bob", result.getName());

        verify(creatureRepository).save(any());
    }
}

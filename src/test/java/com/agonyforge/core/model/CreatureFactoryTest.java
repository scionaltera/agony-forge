package com.agonyforge.core.model;

import com.agonyforge.core.repository.CreatureRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class CreatureFactoryTest {
    @Mock
    private CreatureRepository creatureRepository;

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

        creatureFactory = new CreatureFactory(creatureRepository);
    }

    @Test
    public void testBuildNewPlayer() {
        when(creatureRepository.findByConnectionIsNotNull()).thenReturn(mockCreatureStream());

        Connection connection = new Connection();
        Creature result = creatureFactory.build("Result", connection);

        assertNotNull(result.getConnection());
        assertEquals("Result", result.getName());

        verify(creatureRepository).save(any());
    }

    @Test
    public void testBuildExistingPlayer() {
        when(creatureRepository.findByConnectionIsNotNull()).thenReturn(mockCreatureStream());

        Creature result = creatureFactory.build("Bob", null);

        assertNotNull(result.getConnection());
        assertEquals("Bob", result.getName());

        verify(creatureRepository, never()).save(any());
    }

    private Stream<Creature> mockCreatureStream() {
        String[] names = new String[] {"Alice", "Bob", "Chuck", "Dan"};

        return Arrays
            .stream(names)
            .map(name -> {
                Creature creature = new Creature();
                Connection connection = new Connection();

                creature.setId(UUID.randomUUID());
                creature.setName(name);
                creature.setConnection(connection);

                return creature;
            });
    }
}

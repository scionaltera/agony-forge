package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Gender;
import com.agonyforge.core.model.repository.CreatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WhoCommandTest {
    @Mock
    private CreatureRepository creatureRepository;

    private Creature ch;
    private Output output;

    private WhoCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        ch = new Creature();
        output = new Output();

        List<Creature> creatures = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Creature creature = new Creature();
            Connection connection = new Connection();

            connection.setName("Creature" + i);
            connection.setDisconnected(i % 3 == 0 ? new Date() : null);

            creature.setName("Creature" + i);
            creature.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            creature.setConnection(connection);

            creatures.add(creature);
        }

        when(creatureRepository.findByConnectionIsNotNull()).thenReturn(creatures.stream());

        command = new WhoCommand(creatureRepository);
    }

    @Test
    void testInvoke() {
        command.invoke(ch, output);

        verify(creatureRepository).findByConnectionIsNotNull();

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Who is online:")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Creature0")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Creature1")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Creature2")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Creature3")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Creature4")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("LINK DEAD")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("5 players online.")));
    }

    @Test
    void testSinglePlayer() {
        Creature creature = new Creature();
        Connection connection = new Connection();

        connection.setName("Creature");
        creature.setName("Creature");
        creature.setGender(Gender.NEUTRAL);
        creature.setConnection(connection);

        when(creatureRepository.findByConnectionIsNotNull()).thenReturn(Stream.of(creature));

        command.invoke(ch, output);

        verify(creatureRepository).findByConnectionIsNotNull();

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("1 player online.")));
    }
}

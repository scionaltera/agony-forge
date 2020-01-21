package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LookCommandTest {
    @Mock
    private RoomRepository roomRepository;

    private Creature creature;
    private Creature friend;
    private Output output;

    private LookCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        friend = new Creature();
        creature = new Creature();
        output = new Output();

        command = new LookCommand(roomRepository);
    }

    @Test
    void testInvokeGeneralVoid() {
        command.invoke(creature, output);

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("void")));
    }

    @Test
    void testInvokeGeneralRoom() {
        Room room = new Room();
        room.setId(UUID.randomUUID());
        room.setSequence(0);

        Zone zone = new Zone();
        zone.setId(1L);

        creature.setId(UUID.randomUUID());
        creature.setName("George");
        creature.setRoom(room);

        friend.setId(UUID.randomUUID());
        friend.setName("Darkblade");
        friend.setRoom(room);

        room.setZone(zone);
        room.getCreatures().add(creature);
        room.getCreatures().add(friend);

        command.invoke(creature, output);

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("1#0")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("[green]Darkblade is here.")));
        assertTrue(output.getOutput().stream().noneMatch(line -> line.contains("[green]George is here.")));
    }
}

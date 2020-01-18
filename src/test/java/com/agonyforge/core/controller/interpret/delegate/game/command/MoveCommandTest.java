package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Direction;
import com.agonyforge.core.model.Portal;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class MoveCommandTest {
    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private CommService commService;

    @Mock
    private Interpreter interpreter;

    @Mock
    private Creature creature;

    private MoveCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(interpreter.interpret(any(), any(), anyBoolean())).thenReturn(new Output("LOOK"));

        command = new MoveCommand(creatureRepository, commService, interpreter, Direction.NORTH);
    }

    @Test
    void testInvokeNullRoom() {
        command.invoke(creature, new Output());

        verifyNoInteractions(commService, creatureRepository);
        verify(creature, never()).setRoom(any());
    }

    @Test
    void testInvokeNullPortal() {
        Room room = mock(Room.class);
        Map<Direction, Portal> exits = new HashMap<>();

        when(room.getExits()).thenReturn(exits);
        when(creature.getRoom()).thenReturn(room);

        command.invoke(creature, new Output());

        verifyNoInteractions(commService, creatureRepository);
        verify(creature, never()).setRoom(any());
    }

    @Test
    void testInvoke() {
        Room room = mock(Room.class);
        Room destination = mock(Room.class);
        Portal portal = mock(Portal.class);
        Map<Direction, Portal> exits = new HashMap<>();

        exits.put(Direction.NORTH, portal);

        when(portal.getRoom()).thenReturn(destination);
        when(room.getExits()).thenReturn(exits);
        when(creature.getRoom()).thenReturn(room);

        command.invoke(creature, new Output());

        verify(commService).echoToRoom(eq(room), eq(interpreter), any(Output.class), eq(creature));
        verify(creature).setRoom(eq(destination));
        verify(creatureRepository).save(eq(creature));
        verify(commService).echoToRoom(eq(destination), eq(interpreter), any(Output.class), eq(creature));
    }
}

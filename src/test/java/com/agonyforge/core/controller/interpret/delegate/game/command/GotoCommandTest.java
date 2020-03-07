package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.controller.interpret.delegate.game.binding.PlayerBinding;
import com.agonyforge.core.controller.interpret.delegate.game.binding.RoomInWorldBinding;
import com.agonyforge.core.controller.interpret.delegate.game.binding.RoomInZoneBinding;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GotoCommandTest {
    @Mock
    private CommService commService;

    @Mock
    private Interpreter interpreter;

    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private Output output;

    @Mock
    private Room origin;

    @Mock
    private Room destination;

    @Mock
    private Connection actorConnection;

    @Mock
    private Connection targetConnection;

    @Spy
    private Creature actor;

    @Spy
    private Creature target;

    private GotoCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(creatureRepository.save(any())).thenAnswer(invocation -> {
            Creature creature = invocation.getArgument(0);

            if (creature.getId() == null) {
                creature.setId(UUID.randomUUID());
            }

            return creature;
        });

        actor.setConnection(actorConnection);
        target.setConnection(targetConnection);

        command = new GotoCommand(commService, interpreter, creatureRepository);
    }

    @Test
    void testGotoPlayer() {
        PlayerBinding binding = mock(PlayerBinding.class);

        when(binding.getPlayer()).thenReturn(target);

        actor.setRoom(origin);
        target.setRoom(destination);

        command.invoke(actor, output, binding);

        verify(commService).echoToRoom(eq(origin), eq(interpreter), any(Output.class), eq(actor));
        verify(actor).setRoom(eq(destination));
        verify(creatureRepository).save(eq(actor));
        verify(commService).echoToRoom(eq(destination), eq(interpreter), any(Output.class), eq(actor));
        verify(interpreter).interpret(any(Input.class), any(Connection.class), eq(false));
    }

    @Test
    void testGotoPlayerSameRoom() {
        PlayerBinding binding = mock(PlayerBinding.class);

        when(binding.getPlayer()).thenReturn(target);

        actor.setRoom(origin);
        target.setRoom(origin);

        command.invoke(actor, output, binding);

        verify(commService, never()).echoToRoom(eq(origin), eq(interpreter), any(Output.class), eq(actor));
        verify(actor, never()).setRoom(eq(destination));
        verify(creatureRepository, never()).save(eq(actor));
        verify(commService, never()).echoToRoom(eq(destination), eq(interpreter), any(Output.class), eq(actor));
        verify(interpreter, never()).interpret(any(Input.class), any(Connection.class), eq(false));
    }

    @Test
    void testGotoRoomInZone() {
        RoomInZoneBinding binding = mock(RoomInZoneBinding.class);

        when(binding.getRoom()).thenReturn(destination);

        actor.setRoom(origin);
        target.setRoom(destination);

        command.invoke(actor, output, binding);

        verify(commService).echoToRoom(eq(origin), eq(interpreter), any(Output.class), eq(actor));
        verify(actor).setRoom(eq(destination));
        verify(creatureRepository).save(eq(actor));
        verify(commService).echoToRoom(eq(destination), eq(interpreter), any(Output.class), eq(actor));
        verify(interpreter).interpret(any(Input.class), any(Connection.class), eq(false));
    }

    @Test
    void testGotoRoomInZoneSameRoom() {
        RoomInZoneBinding binding = mock(RoomInZoneBinding.class);

        when(binding.getRoom()).thenReturn(origin);

        actor.setRoom(origin);

        command.invoke(actor, output, binding);

        verify(commService, never()).echoToRoom(eq(origin), eq(interpreter), any(Output.class), eq(actor));
        verify(actor, never()).setRoom(eq(destination));
        verify(creatureRepository, never()).save(eq(actor));
        verify(commService, never()).echoToRoom(eq(destination), eq(interpreter), any(Output.class), eq(actor));
        verify(interpreter, never()).interpret(any(Input.class), any(Connection.class), eq(false));
    }

    @Test
    void testGotoRoomInWorld() {
        RoomInWorldBinding binding = mock(RoomInWorldBinding.class);

        when(binding.getRoom()).thenReturn(destination);

        actor.setRoom(origin);
        target.setRoom(destination);

        command.invoke(actor, output, binding);

        verify(commService).echoToRoom(eq(origin), eq(interpreter), any(Output.class), eq(actor));
        verify(actor).setRoom(eq(destination));
        verify(creatureRepository).save(eq(actor));
        verify(commService).echoToRoom(eq(destination), eq(interpreter), any(Output.class), eq(actor));
        verify(interpreter).interpret(any(Input.class), any(Connection.class), eq(false));
    }

    @Test
    void testGotoRoomInWorldSameRoom() {
        RoomInWorldBinding binding = mock(RoomInWorldBinding.class);

        when(binding.getRoom()).thenReturn(origin);

        actor.setRoom(origin);

        command.invoke(actor, output, binding);

        verify(commService, never()).echoToRoom(eq(origin), eq(interpreter), any(Output.class), eq(actor));
        verify(actor, never()).setRoom(eq(destination));
        verify(creatureRepository, never()).save(eq(actor));
        verify(commService, never()).echoToRoom(eq(destination), eq(interpreter), any(Output.class), eq(actor));
        verify(interpreter, never()).interpret(any(Input.class), any(Connection.class), eq(false));
    }
}

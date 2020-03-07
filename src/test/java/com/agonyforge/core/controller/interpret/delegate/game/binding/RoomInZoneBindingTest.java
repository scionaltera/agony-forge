package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class RoomInZoneBindingTest {
    @Mock
    private Creature actor;

    @Mock
    private Zone zone;

    @Mock
    private Room room;

    @Mock
    private RoomRepository roomRepository;

    private RoomInZoneBinding binding;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(roomRepository.findByZoneAndSequence(eq(zone), eq(3))).thenReturn(Optional.of(room));
        when(actor.getRoom()).thenReturn(room);
        when(room.getZone()).thenReturn(zone);

        binding = new RoomInZoneBinding(roomRepository);
    }

    @Test
    void testBind() {
        boolean result = binding.bind(actor, "3");

        assertTrue(result);
        assertEquals(room, binding.getRoom());
    }

    @Test
    void testBadSequenceNumber() {
        boolean result = binding.bind(actor, "Z");

        assertFalse(result);
        assertNull(binding.getRoom());
    }

    @Test
    void testActorNotInRoom() {
        when(actor.getRoom()).thenReturn(null);

        boolean result = binding.bind(actor, "3");

        assertFalse(result);
        assertNull(binding.getRoom());
    }

    @Test
    void testRoomNotInZone() {
        when(room.getZone()).thenReturn(null);

        boolean result = binding.bind(actor, "3");

        assertFalse(result);
        assertNull(binding.getRoom());
    }

    @Test
    void testBindFailure() {
        boolean result = binding.bind(actor, "2");

        assertFalse(result);
        assertNull(binding.getRoom());
    }
}

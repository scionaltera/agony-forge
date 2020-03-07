package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.RoomRepository;
import com.agonyforge.core.model.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class RoomInWorldBindingTest {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private Creature actor;

    @Mock
    private Room target;

    @Mock
    private Zone zone;

    @Mock
    private Room room;

    private RoomInWorldBinding binding;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(zoneRepository.findById(eq(1L))).thenReturn(Optional.of(zone));
        when(roomRepository.findByZoneAndSequence(eq(zone), eq(3))).thenReturn(Optional.of(room));

        binding = new RoomInWorldBinding(zoneRepository, roomRepository);
    }

    @Test
    void testBind() {
        boolean result = binding.bind(actor, "1#3");

        assertTrue(result);
        assertEquals(room, binding.getRoom());
    }

    @Test
    void testBadFormat() {
        boolean result = binding.bind(actor, "garbage");

        assertFalse(result);
        assertNull(binding.getRoom());
    }

    @Test
    void testBindZoneFailure() {
        boolean result = binding.bind(actor, "2#3");

        assertFalse(result);
        assertNull(binding.getRoom());
    }

    @Test
    void testBindSequenceFailure() {
        boolean result = binding.bind(actor, "1#2");

        assertFalse(result);
        assertNull(binding.getRoom());
    }

    @Test
    void testBindUtterFailure() {
        boolean result = binding.bind(actor, "9#9");

        assertFalse(result);
        assertNull(binding.getRoom());
    }
}

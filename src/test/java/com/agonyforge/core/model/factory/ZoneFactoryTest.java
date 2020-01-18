package com.agonyforge.core.model.factory;

import com.agonyforge.core.model.Portal;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.PortalRepository;
import com.agonyforge.core.model.repository.RoomRepository;
import com.agonyforge.core.model.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.core.model.factory.ZoneFactory.ZONE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ZoneFactoryTest {
    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PortalRepository portalRepository;

    private long nextZoneId = 1;

    private ZoneFactory zoneFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        nextZoneId = 1L;

        when(zoneRepository.save(any())).thenAnswer(invocation -> {
            Zone zone = invocation.getArgument(0);

            if (zone.getId() == null) {
                zone.setId(nextZoneId++);
            }

            return zone;
        });

        when(roomRepository.save(any())).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);

            if (room.getId() == null) {
                room.setId(UUID.randomUUID());
            }

            return room;
        });

        when(roomRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Room> rooms = invocation.getArgument(0);

            rooms.stream()
                .filter(room -> room.getId() == null)
                .forEach(room -> room.setId(UUID.randomUUID()));

            return rooms;
        });

        when(portalRepository.save(any())).thenAnswer(invocation -> {
            Portal portal = invocation.getArgument(0);

            if (portal.getId() == null) {
                portal.setId(UUID.randomUUID());
            }

            return portal;
        });

        zoneFactory = new ZoneFactory(
            zoneRepository,
            roomRepository,
            portalRepository);
    }

    @Test
    void testGetStartZoneEmpty() {
        when(zoneRepository.findById(eq(1L))).thenReturn(Optional.empty());

        Zone result = zoneFactory.getStartZone();

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(zoneRepository).findById(1L);
        verify(zoneRepository, atLeastOnce()).save(any(Zone.class));
    }

    @Test
    void testGetStartZoneExists() {
        Zone zone = new Zone();

        zone.setId(1L);

        when(zoneRepository.findById(eq(1L))).thenReturn(Optional.of(zone));

        Zone result = zoneFactory.getStartZone();

        assertEquals(zone, result);

        verify(zoneRepository).findById(1L);
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    void testBuild() {
        Zone zone = zoneFactory.build();

        assertEquals(ZONE_SIZE, zone.getRooms().size());

        verify(zoneRepository, atLeastOnce()).save(eq(zone));
        verify(roomRepository).saveAll(anyList());
    }
}

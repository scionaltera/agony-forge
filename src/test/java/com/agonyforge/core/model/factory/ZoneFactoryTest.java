package com.agonyforge.core.model.factory;

import com.agonyforge.core.model.Direction;
import com.agonyforge.core.model.Portal;
import com.agonyforge.core.model.PortalFlag;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.PortalRepository;
import com.agonyforge.core.model.repository.RoomRepository;
import com.agonyforge.core.model.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.core.model.factory.ZoneFactory.ZONE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ZoneFactoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneFactoryTest.class);

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PortalRepository portalRepository;

    @Mock
    private Room room;

    @Captor
    private ArgumentCaptor<Portal> portalCaptor;

    private long nextZoneId = 1;

    private ZoneFactory zoneFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        nextZoneId = 1L;

        setUpRepositoryMocks();

        zoneFactory = new ZoneFactory(
            zoneRepository,
            roomRepository,
            portalRepository);
    }

    private void setUpRepositoryMocks() {
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

        when(roomRepository.saveAll(anyCollection())).thenAnswer(invocation -> {
            Collection<Room> rooms = invocation.getArgument(0);

            rooms.stream()
                .filter(room -> room.getId() == null)
                .forEach(room -> room.setId(UUID.randomUUID()));

            return new ArrayList<>(rooms);
        });

        when(portalRepository.save(any())).thenAnswer(invocation -> {
            Portal portal = invocation.getArgument(0);

            if (portal.getId() == null) {
                portal.setId(UUID.randomUUID());
            }

            return portal;
        });
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
        verify(roomRepository).saveAll(anyCollection());
    }

    @Test
    void testConvertZonePortal() {
        Direction direction = Direction.NORTH;
        Portal portal = mock(Portal.class);
        Map<Direction, Portal> exits = new HashMap<>();
        EnumSet<PortalFlag> flags = EnumSet.of(PortalFlag.ZONE_PORTAL);

        exits.put(direction, portal);

        boolean done = false;

        do {
            try {
                Zone zone = zoneFactory.build();

                // ignore the verification effects from building the zone
                // those are covered by other tests
                reset(portalRepository, roomRepository, zoneRepository, portal);

                setUpRepositoryMocks();

                when(room.getExits()).thenReturn(exits);
                when(portal.getRoom()).thenReturn(room);
                when(portal.getFlags()).thenReturn(flags);

                zoneFactory.convertZonePortal(zone, room, direction);
                done = true;
            } catch (IllegalArgumentException e) {
                LOGGER.warn(e.getMessage());
            }
        } while (!done);

        verify(portalRepository, times(2)).save(portalCaptor.capture());
        verify(roomRepository).save(any(Room.class));
        verify(portal).setRoom(any(Room.class));
        verify(portal, never()).setRoom(eq(room));

        Portal reciprocal = portalCaptor.getAllValues().get(0);

        assertEquals(room, reciprocal.getRoom());
        assertEquals(EnumSet.noneOf(PortalFlag.class), reciprocal.getFlags());
    }
}

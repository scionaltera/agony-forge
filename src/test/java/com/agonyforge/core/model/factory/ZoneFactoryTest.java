package com.agonyforge.core.model.factory;

import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ZoneFactoryTest {
    @Mock
    private ZoneRepository zoneRepository;

    private long nextZoneId = 1;

    private ZoneFactory zoneFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        nextZoneId = 1L;

        when(zoneRepository.save(any())).thenAnswer(invocation -> {
            Zone zone = invocation.getArgument(0);

            zone.setId(nextZoneId++);

            return zone;
        });

        zoneFactory = new ZoneFactory(zoneRepository);
    }

    @Test
    void testGetStartZoneEmpty() {
        when(zoneRepository.findById(eq(1L))).thenReturn(Optional.empty());

        Zone result = zoneFactory.getStartZone();

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(zoneRepository).findById(1L);
        verify(zoneRepository).save(any(Zone.class));
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

        assertNotNull(zone);

        verify(zoneRepository).save(eq(zone));
    }
}

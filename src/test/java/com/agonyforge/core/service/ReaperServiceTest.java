package com.agonyforge.core.service;

import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.repository.ConnectionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ReaperServiceTest {
    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @Captor
    private ArgumentCaptor<List<Creature>> creatureListCaptor;

    @Captor
    private ArgumentCaptor<List<Connection>> connectionListCaptor;

    private ReaperService reaperService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Connection dead1c = new Connection();
        dead1c.setDisconnected(new Date());

        Connection dead2c = new Connection();
        dead2c.setDisconnected(new Date());

        Connection dead3c = new Connection();
        dead3c.setDisconnected(new Date());

        Creature dead1 = new Creature();
        dead1.setName("Dead1");
        dead1.setConnection(dead1c);

        Creature dead2 = new Creature();
        dead2.setName("Dead2");
        dead2.setConnection(dead2c);

        when(creatureRepository.findByConnectionDisconnectedIsNotNull())
            .thenReturn(Stream.of(dead1, dead2));

        when(connectionRepository.findByDisconnectedIsNotNull())
            .thenReturn(Stream.of(dead3c));

        reaperService = new ReaperService(creatureRepository, connectionRepository);
    }

    @Test
    public void testReap() {
        reaperService.reapTheDead();

        verify(creatureRepository).deleteInBatch(creatureListCaptor.capture());
        verify(connectionRepository).deleteInBatch(connectionListCaptor.capture());

        List<Creature> deletedCreatures = creatureListCaptor.getValue();
        List<Connection> deletedConnections = connectionListCaptor.getValue();


        assertEquals(2, deletedCreatures.size());
        assertEquals(1, deletedConnections.size());

        deletedCreatures.forEach(creature -> assertNotNull(creature.getConnection().getDisconnected()));
        deletedConnections.forEach(connection -> assertNotNull(connection.getDisconnected()));
    }

    @Test
    public void testEmptyReap() {
        when(creatureRepository.findByConnectionDisconnectedIsNotNull())
            .thenReturn(Stream.empty());
        when(connectionRepository.findByDisconnectedIsNotNull())
            .thenReturn(Stream.empty());

        reaperService.reapTheDead();

        verify(creatureRepository, never()).deleteInBatch(anyIterable());
        verify(connectionRepository, never()).deleteInBatch(anyIterable());
    }
}

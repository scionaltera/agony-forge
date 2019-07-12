package com.agonyforge.demo.service;

import com.agonyforge.demo.model.Connection;
import com.agonyforge.demo.model.Creature;
import com.agonyforge.demo.repository.CreatureRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ReaperServiceTest {
    @Mock
    private CreatureRepository creatureRepository;

    @Captor
    private ArgumentCaptor<List<Creature>> creatureListCaptor;

    private ReaperService reaperService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Connection dead1c = new Connection();
        dead1c.setDisconnected(new Date());

        Connection dead2c = new Connection();
        dead2c.setDisconnected(new Date());

        Creature dead1 = new Creature();
        dead1.setName("Dead1");
        dead1.setConnection(dead1c);

        Creature dead2 = new Creature();
        dead2.setName("Dead2");
        dead2.setConnection(dead2c);

        when(creatureRepository.findByConnectionDisconnectedIsNotNull())
            .thenReturn(Stream.of(dead1, dead2));

        reaperService = new ReaperService(creatureRepository);
    }

    @Test
    public void testReap() {
        reaperService.reapLinkDeadCreatures();

        verify(creatureRepository).deleteInBatch(creatureListCaptor.capture());

        List<Creature> deletedCreatures = creatureListCaptor.getValue();

        assertEquals(2, deletedCreatures.size());
        deletedCreatures.forEach(creature -> assertNotNull(creature.getConnection().getDisconnected()));
    }

    @Test
    public void testEmptyReap() {
        when(creatureRepository.findByConnectionDisconnectedIsNotNull())
            .thenReturn(Stream.empty());

        reaperService.reapLinkDeadCreatures();

        verify(creatureRepository, never()).deleteInBatch(anyIterable());
    }
}

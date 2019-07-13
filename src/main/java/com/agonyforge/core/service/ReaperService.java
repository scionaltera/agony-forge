package com.agonyforge.core.service;

import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.repository.ConnectionRepository;
import com.agonyforge.core.repository.CreatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReaperService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaperService.class);

    private CreatureRepository creatureRepository;
    private ConnectionRepository connectionRepository;

    @Inject
    public ReaperService(CreatureRepository creatureRepository, ConnectionRepository connectionRepository) {
        this.creatureRepository = creatureRepository;
        this.connectionRepository = connectionRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 3600000L)
    public void reapTheDead() {
        List<Creature> linkDead = creatureRepository
            .findByConnectionDisconnectedIsNotNull()
            .collect(Collectors.toList());

        if (!linkDead.isEmpty()) {
            LOGGER.info("Reaping link-dead player{}: {}",
                linkDead.size() == 1 ? "" : "s",
                linkDead
                    .stream()
                    .map(Creature::getName)
                    .collect(Collectors.joining(", ")));

            creatureRepository.deleteInBatch(linkDead);
        }

        List<Connection> orphaned = connectionRepository
            .findByDisconnectedIsNotNull()
            .collect(Collectors.toList());

        if (!orphaned.isEmpty()) {
            LOGGER.info("Reaping orphaned connection{}: {}",
                orphaned.size() == 1 ? "" : "s",
                orphaned
                    .stream()
                    .map(Connection::getSessionId)
                    .collect(Collectors.joining(", ")));

            connectionRepository.deleteInBatch(orphaned);
        }
    }
}

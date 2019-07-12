package com.agonyforge.demo.service;

import com.agonyforge.demo.model.Creature;
import com.agonyforge.demo.repository.CreatureRepository;
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

    @Inject
    public ReaperService(CreatureRepository creatureRepository) {
        this.creatureRepository = creatureRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 3600000L)
    public void reapLinkDeadCreatures() {
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
    }
}

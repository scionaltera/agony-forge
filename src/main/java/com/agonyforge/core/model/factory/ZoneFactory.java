package com.agonyforge.core.model.factory;

import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ZoneFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneFactory.class);

    private ZoneRepository zoneRepository;

    @Inject
    public ZoneFactory(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public Zone getStartZone() {
        return zoneRepository.findById(1L).orElseGet(() -> {
            Zone zone = build();

            LOGGER.warn("Created initial zone: Zone {}", zone.getId());

            return zone;
        });
    }

    public Zone build() {
        return zoneRepository.save(new Zone());
    }
}

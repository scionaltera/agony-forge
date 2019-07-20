package com.agonyforge.core.model;

import com.agonyforge.core.repository.CreatureRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class CreatureFactory {
    private CreatureRepository creatureRepository;

    @Inject
    public CreatureFactory(CreatureRepository creatureRepository) {
        this.creatureRepository = creatureRepository;
    }

    public Creature build(String name, Connection connection) {
        return creatureRepository
            .findByConnectionIsNotNull()
            .filter(c -> !c.getConnection().equals(connection))
            .filter(c -> c.getName().equals(name))
            .findFirst()
            .orElseGet(() -> {
                Creature c = new Creature();

                c.setName(name);
                c.setConnection(connection);

                return creatureRepository.save(c);
            });
    }
}

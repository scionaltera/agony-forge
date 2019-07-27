package com.agonyforge.core.model.repository;

import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface CreatureRepository extends JpaRepository<Creature, UUID> {
    Stream<Creature> findByConnectionIsNotNull();
    Stream<Creature> findByConnectionDisconnectedIsNotNull();
    Optional<Creature> findByConnection(Connection connection);
    Stream<Creature> findByDefinition(CreatureDefinition definition);
}

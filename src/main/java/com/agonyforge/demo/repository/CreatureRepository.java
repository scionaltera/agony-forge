package com.agonyforge.demo.repository;

import com.agonyforge.demo.model.Connection;
import com.agonyforge.demo.model.Creature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface CreatureRepository extends JpaRepository<Creature, UUID> {
    Stream<Creature> findByConnectionIsNotNull();
    Stream<Creature> findByConnectionDisconnectedIsNotNull();
    Optional<Creature> findByConnection(Connection connection);
}

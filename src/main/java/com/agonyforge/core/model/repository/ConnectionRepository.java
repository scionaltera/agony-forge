package com.agonyforge.core.model.repository;

import com.agonyforge.core.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface ConnectionRepository extends JpaRepository<Connection, UUID> {
    Optional<Connection> findBySessionId(String sessionId);
    Stream<Connection> findByDisconnectedIsNotNull();
}

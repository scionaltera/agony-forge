package com.agonyforge.core.repository;

import com.agonyforge.core.model.CreatureDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreatureDefinitionRepository extends JpaRepository<CreatureDefinition, UUID> {
    Optional<CreatureDefinition> findByPlayerIsTrueAndName(String name);
}

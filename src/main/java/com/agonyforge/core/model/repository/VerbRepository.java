package com.agonyforge.core.model.repository;


import com.agonyforge.core.model.Verb;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerbRepository extends JpaRepository<Verb, UUID> {
    Optional<Verb> findFirstByNameIgnoreCaseStartingWith(Sort sort, String name);
}

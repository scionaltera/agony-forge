package com.agonyforge.core.model.repository;

import com.agonyforge.core.model.Portal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PortalRepository extends JpaRepository<Portal, UUID> {
}

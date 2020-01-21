package com.agonyforge.core.model.repository;

import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    Optional<Room> findByZoneAndSequence(Zone zone, int sequence);
}

package com.agonyforge.core.model.factory;

import com.agonyforge.core.model.Coordinate;
import com.agonyforge.core.model.Direction;
import com.agonyforge.core.model.Portal;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.PortalRepository;
import com.agonyforge.core.model.repository.RoomRepository;
import com.agonyforge.core.model.repository.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class ZoneFactory {
    static final int ZONE_SIZE = 100;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneFactory.class);
    private static final Long START_ZONE = 1L;
    private static final Random RANDOM = new Random();

    private ZoneRepository zoneRepository;
    private RoomRepository roomRepository;
    private PortalRepository portalRepository;

    @Inject
    public ZoneFactory(
        ZoneRepository zoneRepository,
        RoomRepository roomRepository,
        PortalRepository portalRepository) {

        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
        this.portalRepository = portalRepository;
    }

    public Zone getStartZone() {
        return zoneRepository.findById(START_ZONE).orElseGet(() -> {
            Zone zone = build();

            LOGGER.warn("Created initial zone: Zone {}", zone.getId());

            return zone;
        });
    }

    public Zone build() {
        final Zone zone = zoneRepository.save(new Zone());
        final Map<Coordinate, Room> space = new HashMap<>();
        final List<Direction> directions = Arrays
            .stream(Direction.values())
            .filter(dir -> !dir.equals(Direction.UP) && !dir.equals(Direction.DOWN))
            .collect(Collectors.toList());
        Coordinate current = new Coordinate(0, 0, 0);
        int sequence = 0;

        LOGGER.info("Placing {} rooms...", ZONE_SIZE);

        do {
            Room room = space.get(current);

            if (room == null) {
                LOGGER.debug("Placing room {} at {}", sequence, current);

                room = new Room(zone, sequence++);
                space.put(current, room);
            }

            Direction direction = directions.get(RANDOM.nextInt(directions.size()));
            current = new Coordinate(
                current.getX() + direction.getX(),
                current.getY() + direction.getY(),
                current.getZ() + direction.getZ());
        } while (space.size() < ZONE_SIZE);

        LOGGER.debug("Placed {} rooms", space.size());

        LOGGER.debug("Linking neighbors...");

        for (Coordinate coordinate : space.keySet()) {
            for (Direction direction : directions) {
                Coordinate neighborCoord = new Coordinate(
                    coordinate.getX() + direction.getX(),
                    coordinate.getY() + direction.getY(),
                    coordinate.getZ() + direction.getZ());
                Room room = space.get(coordinate);
                Room neighbor = space.get(neighborCoord);

                if (neighbor != null) {
                    Portal exit = portalRepository.save(new Portal(neighbor));

                    room.getExits().put(direction, exit);

                    LOGGER.info("Adding exit: {} -{}> {}",
                        room.getSequence(),
                        direction.getName(),
                        neighbor.getSequence());
                }
            }
        }

        LOGGER.debug("Neighbors linked.");

        List<Room> savedRooms = roomRepository.saveAll(space.values());
        zone.setRooms(savedRooms);

        return zoneRepository.save(zone);
    }
}

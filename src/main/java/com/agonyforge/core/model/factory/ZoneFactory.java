package com.agonyforge.core.model.factory;

import com.agonyforge.core.model.Coordinate;
import com.agonyforge.core.model.Direction;
import com.agonyforge.core.model.Portal;
import com.agonyforge.core.model.PortalFlag;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.PortalRepository;
import com.agonyforge.core.model.repository.RoomRepository;
import com.agonyforge.core.model.repository.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class ZoneFactory {
    static final int ZONE_SIZE = 100;
    static final int ZONE_PORTALS = 3;

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

        placeRooms(zone, space, directions);
        linkNeighbors(space, directions);
        placeZonePortals(space, directions);

        List<Room> savedRooms = roomRepository.saveAll(space.values());
        zone.setRooms(savedRooms);

        return zoneRepository.save(zone);
    }

    public void convertZonePortal(Zone generated, Room room, Direction direction) {
        Portal zonePortal = room.getExits().get(direction);
        Direction reciprocal = Direction.valueOf(direction.getOpposite().toUpperCase());
        List<Room> generatedRooms = generated.getRooms().stream()
            .filter(r -> r.getExits().size() == 1)
            .filter(r -> !r.getExits().containsKey(reciprocal))
            .collect(Collectors.toList());

        // TODO may need to edit and "fix" a room or relax the criteria because this seems to happen a lot
        if (generatedRooms.isEmpty()) {
            throw new IllegalArgumentException("Destination Zone has no rooms to link into!");
        }

        Collections.shuffle(generatedRooms);
        Room destination = generatedRooms.get(0);

        // add reciprocal portal to destination room
        Portal reciprocalPortal = portalRepository.save(new Portal(room));

        destination.getExits().put(reciprocal, reciprocalPortal);
        roomRepository.save(destination);

        // convert zone portal into normal portal
        zonePortal.setRoom(destination);
        zonePortal.getFlags().remove(PortalFlag.ZONE_PORTAL);
        portalRepository.save(zonePortal);
    }

    private void placeRooms(Zone zone, Map<Coordinate, Room> space, List<Direction> directions) {
        Coordinate current = new Coordinate(0, 0, 0);
        int sequence = 0;

        LOGGER.debug("Placing {} rooms...", ZONE_SIZE);

        do {
            Room room = space.get(current);

            if (room == null) {
                LOGGER.debug("Placing room {} at {}", sequence, current);

                room = roomRepository.save(new Room(zone, sequence++));
                space.put(current, room);
            }

            Direction direction = directions.get(RANDOM.nextInt(directions.size()));
            current = new Coordinate(
                current.getX() + direction.getX(),
                current.getY() + direction.getY(),
                current.getZ() + direction.getZ());
        } while (space.size() < ZONE_SIZE);

        LOGGER.info("Placed {} rooms", space.size());
    }

    private void linkNeighbors(Map<Coordinate, Room> space, List<Direction> directions) {
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

                    LOGGER.debug("Adding exit: {} -{}> {}",
                        room.getSequence(),
                        direction.getName(),
                        neighbor.getSequence());
                }
            }
        }

        LOGGER.info("Neighbors linked");
    }

    private void placeZonePortals(Map<Coordinate, Room> space, List<Direction> directions) {
        LOGGER.debug("Placing zone portals...");

        int placed = 0;
        List<Room> deadEnds = space.values().stream()
            .filter(room -> room.getExits().size() == 1)
            .collect(Collectors.toList());

        Collections.shuffle(deadEnds);

        for (int i = 0; i < ZONE_PORTALS && i < deadEnds.size(); i++) {
            Room room = deadEnds.get(i);
            Portal zonePortal = portalRepository.save(new Portal(room, EnumSet.of(PortalFlag.ZONE_PORTAL)));
            List<Direction> valid = new ArrayList<>(directions);

            valid.removeAll(room.getExits().keySet());

            Collections.shuffle(valid);
            Direction direction = valid.get(0);

            room.getExits().put(direction, zonePortal);

            LOGGER.debug("Adding zone portal: {} -{}-> [zone TBD]",
                room.getSequence(),
                direction.getName());

            placed++;
        }

        LOGGER.info("Placed {} zone portals", placed);
    }
}

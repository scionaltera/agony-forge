package com.agonyforge.core.model.factory;

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
import java.util.ArrayList;
import java.util.List;

@Component
public class ZoneFactory {
    static final int ZONE_SIZE = 100;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneFactory.class);
    private static final Long START_ZONE = 1L;

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
        Zone zone = zoneRepository.save(new Zone());
        List<Room> rooms = new ArrayList<>();

        for (int i = 0; i < ZONE_SIZE; i++) {
            Room room = roomRepository.save(new Room(zone, i));

            if (i > 0) {
                Room last = rooms.get(i - 1);
                Portal forwardExit = portalRepository.save(new Portal(last));
                Portal reciprocalExit = portalRepository.save(new Portal(room));

                last.getExits().put(Direction.EAST, reciprocalExit);
                room.getExits().put(Direction.WEST, forwardExit);
            }

            rooms.add(room);
        }

        zone.setRooms(roomRepository.saveAll(rooms));

        return zoneRepository.save(zone);
    }
}

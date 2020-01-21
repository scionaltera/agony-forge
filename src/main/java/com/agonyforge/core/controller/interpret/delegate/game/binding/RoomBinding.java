package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.RoomRepository;
import com.agonyforge.core.model.repository.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Scope(scopeName = "prototype")
@BindingDescription("room identifier")
public class RoomBinding implements ArgumentBinding {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomBinding.class);
    private static final Pattern ROOM_ID_PATTERN = Pattern.compile("(\\d+)#(\\d+)");

    private ZoneRepository zoneRepository;
    private RoomRepository roomRepository;
    private Room room;

    @Inject
    public RoomBinding(ZoneRepository zoneRepository, RoomRepository roomRepository) {
        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
    }

    // TODO split this into RoomLocalBinding and RoomZoneBinding or something?
    // then GOTO could have two invocations and the separation of concerns would be better, more testable

    @Override
    public boolean bind(Creature actor, String token) {
        Matcher m = ROOM_ID_PATTERN.matcher(token);

        if (m.matches()) {
            Optional<Room> roomOptional = getRoomFromZoneAndSequence(token);

            if (roomOptional.isPresent()) {
                room = roomOptional.get();
                return true;
            }
        } else {
            Optional<Room> roomOptional = getRoomFromSequence(actor, token);

            if (roomOptional.isPresent()) {
                room = roomOptional.get();
                return true;
            }
        }

        return false;
    }

    private Optional<Room> getRoomFromSequence(Creature actor, String token) {
        try {
            int roomSequence = Integer.parseInt(token);
            long zoneId = actor.getRoom().getZone().getId();

            Optional<Zone> zoneOptional = zoneRepository.findById(zoneId);

            if (!zoneOptional.isPresent()) {
                return Optional.empty();
            }

            Optional<Room> roomOptional = roomRepository.findByZoneAndSequence(zoneOptional.get(), roomSequence);

            if (roomOptional.isPresent()) {
                room = roomOptional.get();
                return Optional.of(room);
            }
        } catch (NumberFormatException e) {
            LOGGER.debug("Couldn't parse {} into int", token);
        } catch (NullPointerException e) {
            LOGGER.debug("Creature {} is not in a room, or room is not in a zone", actor.getName());
        }

        return Optional.empty();
    }

    private Optional<Room> getRoomFromZoneAndSequence(String token) {
        int roomSequence;
        long zoneId;
        Matcher matcher = ROOM_ID_PATTERN.matcher(token);

        if (matcher.matches()) {
            zoneId = Integer.parseInt(matcher.group(1));
            roomSequence = Integer.parseInt(matcher.group(2));
        } else {
            return Optional.empty();
        }

        Optional<Zone> zoneOptional = zoneRepository.findById(zoneId);

        if (!zoneOptional.isPresent()) {
            return Optional.empty();
        }

        return roomRepository.findByZoneAndSequence(zoneOptional.get(), roomSequence);
    }

    public Room getRoom() {
        return room;
    }
}

package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.RoomRepository;
import com.agonyforge.core.model.repository.ZoneRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Scope(scopeName = "prototype")
@BindingDescription("zone#room number")
public class RoomInWorldBinding implements ArgumentBinding {
    private static final Pattern ROOM_ID_PATTERN = Pattern.compile("(\\d+)#(\\d+)");

    private ZoneRepository zoneRepository;
    private RoomRepository roomRepository;
    private Room room;

    @Inject
    public RoomInWorldBinding(ZoneRepository zoneRepository, RoomRepository roomRepository) {
        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public boolean bind(Creature actor, String token) {
        int roomSequence;
        long zoneId;
        Matcher matcher = ROOM_ID_PATTERN.matcher(token);

        if (matcher.matches()) {
            zoneId = Integer.parseInt(matcher.group(1));
            roomSequence = Integer.parseInt(matcher.group(2));
        } else {
            return false;
        }

        Optional<Zone> zoneOptional = zoneRepository.findById(zoneId);

        if (!zoneOptional.isPresent()) {
            return false;
        }

        Optional<Room> roomOptional = roomRepository.findByZoneAndSequence(zoneOptional.get(), roomSequence);

        if (roomOptional.isPresent()) {
            room = roomOptional.get();
            return true;
        }

        return false;
    }

    public Room getRoom() {
        return room;
    }
}

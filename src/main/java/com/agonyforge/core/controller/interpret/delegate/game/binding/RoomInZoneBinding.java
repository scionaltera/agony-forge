package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
@Scope(scopeName = "prototype")
@BindingDescription("room number")
public class RoomInZoneBinding implements ArgumentBinding {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomInZoneBinding.class);

    private RoomRepository roomRepository;
    private Room room;

    @Inject
    public RoomInZoneBinding(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public boolean bind(Creature actor, String token) {
        try {
            int roomSequence = Integer.parseInt(token);
            Zone zone = actor.getRoom().getZone();

            Optional<Room> roomOptional = roomRepository.findByZoneAndSequence(zone, roomSequence);

            if (roomOptional.isPresent()) {
                room = roomOptional.get();
                return true;
            }
        } catch (NumberFormatException e) {
            LOGGER.debug("Couldn't parse {} into int", token);
        } catch (NullPointerException e) {
            LOGGER.debug("Creature {} is not in a room, or room is not in a zone", actor.getName());
        }

        return false;
    }

    public Room getRoom() {
        return room;
    }
}

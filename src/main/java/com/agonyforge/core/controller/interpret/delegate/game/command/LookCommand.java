package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.PortalFlag;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.factory.ZoneFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Component
public class LookCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(LookCommand.class);

    private ZoneFactory zoneFactory;

    @Inject
    public LookCommand(ZoneFactory zoneFactory) {
        this.zoneFactory = zoneFactory;
    }

    @Transactional
    @CommandDescription("Describes the room you are in")
    public void invoke(Creature actor, Output output) {
        Room room = actor.getRoom();

        if (room == null) {
            output.append("[black]You are in the void. All you can see is inky blackness.");
            return;
        }

        output
            .append(String.format("[yellow]&lsqb;%s#%s&rsqb; A Room", room.getZone().getId(), room.getSequence()))
            .append("[dwhite]Room description.");

        output.append(buildExits(room));

        room.getCreatures()
            .stream()
            .filter(creature -> !creature.equals(actor))
            .forEach(creature -> output.append(String.format("[green]%s is here.", creature.getName())));
    }

    private String buildExits(Room room) {
        StringBuilder buf = new StringBuilder("[cyan]Exits: ");

        if (!room.getExits().isEmpty()) {
            room.getExits().keySet()
                .stream()
                .sorted()
                .forEach(direction -> {
                    if (room.getExits().get(direction).getFlags().contains(PortalFlag.ZONE_PORTAL)) {
                        boolean done = false;

                        do {
                            try {
                                Zone zone = zoneFactory.build(); // TODO doing this twice will create two zones
                                zoneFactory.convertZonePortal(zone, room, direction);
                                done = true;
                            } catch (IllegalArgumentException e) {
                                // if we generate a zone that can't be linked into
                                // we need to do it again until it works
                                LOGGER.warn(e.getMessage());
                            }
                        } while (!done);
                    }

                    buf.append(direction.getName());
                    buf.append(" ");
                });
        } else {
            buf.append("none");
        }

        return buf.toString().trim();
    }
}

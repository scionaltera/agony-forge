package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.repository.RoomRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Component
public class LookCommand {
    private RoomRepository roomRepository;

    @Inject
    public LookCommand(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
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

        StringBuilder buf = new StringBuilder("[cyan]Exits: ");

        if (!room.getExits().isEmpty()) {
            room.getExits().keySet()
                .stream()
                .sorted()
                .forEach(direction -> {
                    buf.append(direction.getName());
                    buf.append(" ");
                });
        } else {
            buf.append("none");
        }

        output.append(buf.toString().trim());
    }
}

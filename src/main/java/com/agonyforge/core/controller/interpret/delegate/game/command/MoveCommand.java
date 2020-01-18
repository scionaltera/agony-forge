package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Direction;
import com.agonyforge.core.model.Portal;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;

import javax.inject.Inject;
import javax.transaction.Transactional;

public class MoveCommand {
    private CreatureRepository creatureRepository;
    private CommService commService;
    private Interpreter interpreter;
    private Direction direction;

    @Inject
    public MoveCommand(
        CreatureRepository creatureRepository,
        CommService commService,
        Interpreter interpreter,
        Direction direction) {

        this.creatureRepository = creatureRepository;
        this.commService = commService;
        this.interpreter = interpreter;
        this.direction = direction;
    }

    @Transactional
    @CommandDescription("Moves you in a direction")
    public void invoke(Creature actor, Output output) {
        Room room = actor.getRoom();

        if (room == null) {
            output.append("[black]You are in the void. You can't tell whether you are moving or not.");
            return;
        }

        Portal exit = room.getExits().get(direction);

        if (exit == null) {
            output.append("[default]Alas, you cannot go that way.");
            return;
        }

        commService.echoToRoom(
            room,
            interpreter,
            new Output(String.format("[default]%s leaves to the %s.", actor.getName(), direction.getName())),
            actor
        );

        actor.setRoom(exit.getRoom());
        creatureRepository.save(actor);

        commService.echoToRoom(
            exit.getRoom(),
            interpreter,
            new Output(String.format("[default]%s arrives from the %s.", actor.getName(), direction.getOpposite())),
            actor
        );

        output.append(interpreter.interpret(new Input("look"), actor.getConnection(), false));
    }
}

package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.controller.interpret.delegate.game.binding.RoomBinding;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Component
public class GotoCommand {
    private CommService commService;
    private Interpreter interpreter;
    private CreatureRepository creatureRepository;

    @Inject
    public GotoCommand(
        CommService commService,
        Interpreter interpreter,
        CreatureRepository creatureRepository) {

        this.commService = commService;
        this.interpreter = interpreter;
        this.creatureRepository = creatureRepository;
    }

//    @Transactional
//    @CommandDescription("Teleports you to a player of your choosing")
//    public void invoke(Creature actor, Output output, CreatureBinding creatureBinding) {
//
//    }

    @Transactional
    @CommandDescription("Teleports you to a room of your choosing")
    public void invoke(Creature actor, Output output, RoomBinding roomBinding) {
        Room origin = actor.getRoom();
        Room destination = roomBinding.getRoom();

        commService.echoToRoom(
            origin,
            interpreter,
            new Output(
                String.format(
                    "[yellow]%s disappears in a blinding flash of light![yellow]",
                    actor.getName())),
            actor);

        actor.setRoom(destination);
        actor = creatureRepository.save(actor);

        commService.echoToRoom(
            destination,
            interpreter,
            new Output(
                String.format(
                    "[yellow]%s appears in a blinding flash of light![yellow]",
                    actor.getName())),
            actor);

        output.append(interpreter.interpret(new Input("look"), actor.getConnection(), false));
    }
}

package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.controller.interpret.delegate.game.binding.QuotedStringBinding;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.service.CommService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Component
public class SayCommand {
    private CommService commService;
    private Interpreter interpreter;

    @Inject
    public SayCommand(CommService commService, Interpreter interpreter) {
        this.commService = commService;
        this.interpreter = interpreter;
    }

    @Transactional
    @CommandDescription("Send a message to other players in the same room")
    public void invoke(Creature actor, Output output, QuotedStringBinding quotedStringBinding) {
        String nonBreakingQuote = quotedStringBinding.getToken().replaceAll("\\s", "&nbsp;");
        Room room = actor.getRoom();

        if (room == null) {
            output.append("[black]Your words dissipate noiselessly into the void.");
            return;
        }

        commService.echoToRoom(
            room,
            interpreter,
            new Output(
                String.format(
                    "[cyan]%s says '%s[cyan]'",
                    actor.getName(),
                    nonBreakingQuote)),
            actor);

        output.append(String.format("[cyan]You say '%s[cyan]'", nonBreakingQuote));
    }
}

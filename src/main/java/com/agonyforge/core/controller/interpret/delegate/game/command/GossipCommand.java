package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.controller.interpret.delegate.game.binding.QuotedStringBinding;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.service.CommService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Component
public class GossipCommand {
    private CommService commService;
    private Interpreter interpreter;

    @Inject
    public GossipCommand(CommService commService, Interpreter interpreter) {
        this.commService = commService;
        this.interpreter = interpreter;
    }

    @Transactional
    @CommandDescription("Send a message to all other players")
    public void invoke(Creature actor, Output output, QuotedStringBinding quotedStringBinding) {
        String nonBreakingQuote = quotedStringBinding.getToken().replaceAll("\\s", "&nbsp;");

        commService.echoToWorld(
            new Output(
                String.format(
                    "[green]%s gossips '%s[green]'",
                    actor.getName(),
                    nonBreakingQuote)),
            interpreter,
            actor);

        output.append(String.format("[green]You gossip '%s[green]'", nonBreakingQuote));
    }
}

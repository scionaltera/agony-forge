package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Component
public class CrashCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrashCommand.class);

    private final CommService commService;
    private final Interpreter interpreter;

    @Inject
    public CrashCommand(
        CommService commService,
        Interpreter interpreter) {

        this.commService = commService;
        this.interpreter = interpreter;
    }

    @Transactional
    @CommandDescription("Throws an exception")
    public void invoke(Creature actor) {
        crashGame(actor);
    }

    private void crashGame(Creature actor) {
        commService.echoToWorld(
            new Output(String.format(
                "[red]%s is trying to crash the game! Watch out!",
                actor.getName())),
            interpreter,
            actor);

        // The player won't see the output returned by this method because the exception will interrupt it.
        // There's a decent chance they'll see it if we echo it to them though, if the system handles the
        // exception properly.
        commService.echo(
            actor,
            interpreter,
            new Output("[yellow]Throwing a RuntimeException. Hope you know what you're doing!"));

        LOGGER.error("{} is using the CRASH command to throw an exception!", actor.getName());

        throw new RuntimeException(
            String.format(
                "%s is crashing the game!",
                actor.getName()));
    }
}

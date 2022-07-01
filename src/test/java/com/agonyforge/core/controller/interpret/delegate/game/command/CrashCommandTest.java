package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class CrashCommandTest {
    @Mock
    private CommService commService;

    @Mock
    private Interpreter interpreter;

    private Creature actor;

    private CrashCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        actor = new Creature();
        actor.setName("Puppers");

        command = new CrashCommand(commService, interpreter);
    }

    @Test
    void testInvoke() {
        Output output = new Output();
        boolean exceptionCaught = false;

        try {
            command.invoke(actor, output);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        }

        verify(commService).echoToWorld(any(Output.class), eq(interpreter), eq(actor));
        verify(commService).echo(eq(actor), eq(interpreter), any(Output.class));
        assertTrue(exceptionCaught);
    }
}

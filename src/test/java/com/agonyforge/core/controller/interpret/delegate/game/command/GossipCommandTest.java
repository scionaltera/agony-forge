package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.controller.interpret.delegate.game.binding.QuotedStringBinding;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class GossipCommandTest {
    @Mock
    private CommService commService;

    @Mock
    private Interpreter interpreter;

    @Captor
    private ArgumentCaptor<Output> outputCaptor;

    private Output output;
    private Creature creature;

    private GossipCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        output = new Output();
        creature = new Creature();

        creature.setName("Staniel");

        command = new GossipCommand(commService, interpreter);
    }

    @Test
    void testInvoke() {
        QuotedStringBinding quote = new QuotedStringBinding();

        quote.bind(creature, "Hello friends!");

        command.invoke(creature, output, quote);

        verify(commService).echoToWorld(outputCaptor.capture(), eq(interpreter), eq(creature));

        Output worldMessage = outputCaptor.getValue();

        assertEquals(1, worldMessage.getOutput().size());
        assertTrue(worldMessage.getOutput().stream().anyMatch(line -> line.equals("[green]Staniel gossips 'Hello&nbsp;friends![green]'")));

        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().stream().anyMatch(line -> line.equals("[green]You gossip 'Hello&nbsp;friends![green]'")));
    }
}

package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.controller.interpret.delegate.game.binding.QuotedStringBinding;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Room;
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
import static org.mockito.Mockito.verifyNoInteractions;

class SayCommandTest {
    @Mock
    private CommService commService;

    @Mock
    private Interpreter interpreter;

    @Captor
    private ArgumentCaptor<Output> outputCaptor;

    private Output output;
    private Creature creature;
    private Room room;

    private SayCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        output = new Output();
        creature = new Creature();
        room = new Room();

        creature.setName("Staniel");

        command = new SayCommand(commService, interpreter);
    }

    @Test
    void testInvokeVoid() {
        QuotedStringBinding quote = new QuotedStringBinding();

        quote.bind(creature, "Halp!");

        command.invoke(creature, output, quote);

        verifyNoInteractions(commService);
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("[black]")));
    }

    @Test
    void testInvoke() {
        QuotedStringBinding quote = new QuotedStringBinding();

        creature.setRoom(room);
        quote.bind(creature, "Hello friends!");

        command.invoke(creature, output, quote);

        verify(commService).echoToRoom(
            eq(room),
            eq(interpreter),
            outputCaptor.capture(),
            eq(creature));

        Output message = outputCaptor.getValue();

        assertEquals(1, message.getOutput().size());
        assertTrue(message.getOutput().stream().anyMatch(line -> line.equals("[cyan]Staniel says 'Hello&nbsp;friends![cyan]'")));

        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().stream().anyMatch(line -> line.equals("[cyan]You say 'Hello&nbsp;friends![cyan]'")));
    }
}

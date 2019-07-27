package com.agonyforge.core.controller.interpret.delegate;

import com.agonyforge.core.config.LoginConfiguration;
import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultInGameInterpreterDelegateTest {
    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private Interpreter primary;

    @Mock
    private CommService commService;

    private DefaultInGameInterpreterDelegate interpreter;
    private Creature me = new Creature();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        LoginConfiguration loginConfiguration = new LoginConfigurationBuilder().build();

        me.setName("Scion");

        when(creatureRepository.findByConnection(any())).thenReturn(Optional.of(me));
        when(primary.prompt(any())).thenAnswer(invocation -> {
            Connection connection = invocation.getArgument(0);

            return interpreter.prompt(primary, connection);
        });

        interpreter = new DefaultInGameInterpreterDelegate(
            creatureRepository,
            loginConfiguration,
            commService
        );
    }

    @Test
    public void testInterpret() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Hello!");

        Output output = interpreter.interpret(primary, input, connection);

        assertTrue(output.toString().contains("You gossip"));
        assertTrue(output.toString().contains(input.toString()));

        verify(commService).echoToWorld(any(), eq(primary), eq(me));
    }

    @Test
    public void testInterpretNoCreature() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Hello!");

        when(creatureRepository.findByConnection(any())).thenReturn(Optional.empty());

        try {
            interpreter.interpret(primary, input, connection);

            fail("Required exception was not thrown");
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().startsWith("Unable to find Creature"));
        }
    }
}
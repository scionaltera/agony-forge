package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.delegate.creation.CharacterCreationInterpreterDelegate;
import com.agonyforge.core.controller.interpret.delegate.game.InGameInterpreterDelegate;
import com.agonyforge.core.controller.interpret.delegate.login.LoginInterpreterDelegate;
import com.agonyforge.core.model.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.agonyforge.core.controller.interpret.PrimaryConnectionState.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PrimaryInterpreterTest {
    @Mock
    private LoginInterpreterDelegate loginInterpreterDelegate;

    @Mock
    private CharacterCreationInterpreterDelegate characterCreationInterpreterDelegate;

    @Mock
    private InGameInterpreterDelegate inGameInterpreterDelegate;

    private PrimaryInterpreter primary;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        primary = new PrimaryInterpreter(
            loginInterpreterDelegate,
            characterCreationInterpreterDelegate,
            inGameInterpreterDelegate
        );

        // Have to do the expectations after instantiating the class under test
        // in this case because we need a matcher for the class under test.

        when(loginInterpreterDelegate.interpret(eq(primary), any(), any(), anyBoolean())).thenReturn(new Output("Login!"));
        when(characterCreationInterpreterDelegate.interpret(eq(primary), any(), any(), anyBoolean())).thenReturn(new Output("Create!"));
        when(inGameInterpreterDelegate.interpret(eq(primary), any(), any(), anyBoolean())).thenReturn(new Output("In Game!"));

        when(loginInterpreterDelegate.prompt(eq(primary), any())).thenReturn(new Output("Login Prompt!"));
        when(characterCreationInterpreterDelegate.prompt(eq(primary), any())).thenReturn(new Output("Create Prompt!"));
        when(inGameInterpreterDelegate.prompt(eq(primary), any())).thenReturn(new Output("In Game Prompt!"));
    }

    @Test
    void testInterpretLogin() {
        Connection connection = new Connection();
        Input input = new Input();

        input.setInput("Input!");
        connection.setPrimaryState(LOGIN);

        Output output = primary.interpret(input, connection);

        verify(loginInterpreterDelegate).interpret(eq(primary), eq(input), eq(connection), eq(true));
        verifyNoInteractions(characterCreationInterpreterDelegate, inGameInterpreterDelegate);

        assertEquals("Login!", output.toString());
    }

    @Test
    void testInterpretCreate() {
        Connection connection = new Connection();
        Input input = new Input();

        input.setInput("Input!");
        connection.setPrimaryState(CREATION);

        Output output = primary.interpret(input, connection);

        verify(characterCreationInterpreterDelegate).interpret(eq(primary), eq(input), eq(connection), eq(true));
        verifyNoInteractions(loginInterpreterDelegate, inGameInterpreterDelegate);

        assertEquals("Create!", output.toString());
    }

    @Test
    void testInterpretInGame() {
        Connection connection = new Connection();
        Input input = new Input();

        input.setInput("Input!");
        connection.setPrimaryState(IN_GAME);

        Output output = primary.interpret(input, connection);

        verify(inGameInterpreterDelegate).interpret(eq(primary), eq(input), eq(connection), eq(true));
        verifyNoInteractions(loginInterpreterDelegate, characterCreationInterpreterDelegate);

        assertEquals("In Game!", output.toString());
    }

    @Test
    void testInterpretDisconnected() {
        Connection connection = new Connection();
        Input input = new Input();

        input.setInput("Foo");
        connection.setPrimaryState(DISCONNECTED);

        Output output = primary.interpret(input, connection);

        verifyNoInteractions(loginInterpreterDelegate, characterCreationInterpreterDelegate, inGameInterpreterDelegate);

        assertEquals("", output.toString());
    }

    @Test
    void testPromptLogin() {
        Connection connection = new Connection();

        connection.setPrimaryState(LOGIN);

        Output output = primary.prompt(connection);

        verify(loginInterpreterDelegate).prompt(eq(primary), eq(connection));
        verifyNoInteractions(characterCreationInterpreterDelegate, inGameInterpreterDelegate);

        assertEquals("Login Prompt!", output.toString());
    }

    @Test
    void testPromptCreate() {
        Connection connection = new Connection();

        connection.setPrimaryState(CREATION);

        Output output = primary.prompt(connection);

        verify(characterCreationInterpreterDelegate).prompt(eq(primary), eq(connection));
        verifyNoInteractions(loginInterpreterDelegate, inGameInterpreterDelegate);

        assertEquals("Create Prompt!", output.toString());
    }

    @Test
    void testPromptInGame() {
        Connection connection = new Connection();

        connection.setPrimaryState(IN_GAME);

        Output output = primary.prompt(connection);

        verify(inGameInterpreterDelegate).prompt(eq(primary), eq(connection));
        verifyNoInteractions(loginInterpreterDelegate, characterCreationInterpreterDelegate);

        assertEquals("In Game Prompt!", output.toString());
    }

    @Test
    void testPromptDisconnected() {
        Connection connection = new Connection();

        connection.setPrimaryState(DISCONNECTED);

        Output output = primary.prompt(connection);

        verifyNoInteractions(loginInterpreterDelegate, characterCreationInterpreterDelegate, inGameInterpreterDelegate);

        assertEquals("", output.toString());
    }
}

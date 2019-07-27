package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.delegate.CharacterCreationInterpreterDelegate;
import com.agonyforge.core.controller.interpret.delegate.InGameInterpreterDelegate;
import com.agonyforge.core.controller.interpret.delegate.LoginInterpreterDelegate;
import com.agonyforge.core.model.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class PrimaryInterpreter extends BaseInterpreter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryInterpreter.class);

    private LoginInterpreterDelegate loginInterpreter;
    private CharacterCreationInterpreterDelegate characterCreationInterpreterDelegate;
    private InGameInterpreterDelegate inGameInterpreter;

    @Inject
    public PrimaryInterpreter(
        LoginInterpreterDelegate loginInterpreterDelegate,
        CharacterCreationInterpreterDelegate characterCreationInterpreterDelegate,
        InGameInterpreterDelegate inGameInterpreterDelegate) {

        this.loginInterpreter = loginInterpreterDelegate;
        this.characterCreationInterpreterDelegate = characterCreationInterpreterDelegate;
        this.inGameInterpreter = inGameInterpreterDelegate;
    }

    @Override
    public Output interpret(Input input, Connection connection) {
        PrimaryConnectionState primaryState = connection.getPrimaryState();

        switch (primaryState) {
            case LOGIN: return loginInterpreter.interpret(this, input, connection);
            case CREATION: return characterCreationInterpreterDelegate.interpret(this, input, connection);
            case IN_GAME: return inGameInterpreter.interpret(this, input, connection);
            case DISCONNECTED: return new Output("");
            default:
                LOGGER.error("Reached default state in interpret()!");
                return new Output("[red]Oops! Something went wrong. The error has been logged.");
        }
    }

    @Override
    public Output prompt(Connection connection) {
        PrimaryConnectionState primaryState = connection.getPrimaryState();

        switch (primaryState) {
            case LOGIN: return loginInterpreter.prompt(this, connection);
            case CREATION: return characterCreationInterpreterDelegate.prompt(this, connection);
            case IN_GAME: return inGameInterpreter.prompt(this, connection);
            case DISCONNECTED: return new Output("");
            default:
                LOGGER.error("Reached default state in prompt()!");
                return new Output("[red]Oops! Something went wrong. The error has been logged.");
        }
    }
}

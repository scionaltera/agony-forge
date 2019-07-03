package com.agonyforge.demo;

import com.agonyforge.core.config.LoginConfiguration;
import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.InGameInterpreterDelegate;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.repository.CreatureRepository;
import org.springframework.stereotype.Component;

@Component
public class DemoInGameInterpreterDelegate implements InGameInterpreterDelegate {
    private CreatureRepository creatureRepository;
    private LoginConfiguration loginConfiguration;

    public DemoInGameInterpreterDelegate(
        CreatureRepository creatureRepository,
        LoginConfiguration loginConfiguration) {

        this.creatureRepository = creatureRepository;
        this.loginConfiguration = loginConfiguration;
    }

    @Override
    public Output interpret(Interpreter primary, Input input, Connection connection) {
        Output output = new Output();
        Creature creature = creatureRepository
            .findByConnection(connection)
            .orElseThrow(() -> new NullPointerException("Unable to find Creature for Connection " + connection.getId()));

        output
            .append("[dmagenta]You gossip '" + input.toString() + "[dmagenta]'")
            .append(primary.prompt(connection));
        primary.echoToWorld(new Output("[dmagenta]" + connection.getName() + " gossips '" + input.toString() + "[dmagenta]'"), creature);

        return output;
    }

    @Override
    public Output prompt(Interpreter primary, Connection connection) {
        return new Output("", loginConfiguration.getPrompt("inGame", connection));
    }
}

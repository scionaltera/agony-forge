package com.agonyforge.core.controller.interpret.delegate.game;

import com.agonyforge.core.config.LoginConfiguration;
import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.InvokerService;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultInGameInterpreterDelegate implements InGameInterpreterDelegate {
    private CreatureRepository creatureRepository;
    private LoginConfiguration loginConfiguration; // TODO need to break this configuration apart
    private InvokerService invokerService;

    public DefaultInGameInterpreterDelegate(
        CreatureRepository creatureRepository,
        LoginConfiguration loginConfiguration,
        InvokerService invokerService) {

        this.creatureRepository = creatureRepository;
        this.loginConfiguration = loginConfiguration;
        this.invokerService = invokerService;
    }

    @Override
    public Output interpret(Interpreter primary, Input input, Connection connection) {
        return interpret(primary, input, connection, true);
    }

    @Override
    public Output interpret(Interpreter primary, Input input, Connection connection, boolean showPrompt) {
        Output output = new Output();
        Creature creature = creatureRepository
            .findByConnection(connection)
            .orElseThrow(() -> new NullPointerException("Unable to find Creature for Connection " + connection.getId()));

        List<String> tokens = Arrays
            .stream(QuotingSplitter.split(HtmlUtils.htmlUnescape(input.getInput())))
            .map(String::trim)
            .map(token -> {
                if (token.contains(" ")) {
                    return token;
                }

                return token
                    .toUpperCase()
                    .replaceAll("[^A-Z0-9]", "");
            })
            .filter(token -> !"".equals(token))
            .collect(Collectors.toList());

        invokerService.invoke(creature, output, input.getInput(), tokens);

        if (showPrompt) {
            output.append(primary.prompt(connection));
        }

        return output;
    }

    @Override
    public Output prompt(Interpreter primary, Connection connection) {
        return new Output("", loginConfiguration.getPrompt("inGame", connection));
    }
}

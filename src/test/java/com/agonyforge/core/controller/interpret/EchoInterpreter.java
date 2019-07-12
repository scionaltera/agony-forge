package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.repository.CreatureRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class EchoInterpreter extends BaseInterpreter {
    @Inject
    public EchoInterpreter(CreatureRepository creatureRepository, SimpMessagingTemplate simpMessagingTemplate) {
        super(creatureRepository, simpMessagingTemplate);
    }

    @Override
    public Output interpret(Input input, Connection connection) {
        return new Output("[cyan]" + input).append(prompt(connection));
    }

    @Override
    public Output prompt(Connection connection) {
        return new Output("[default]> ");
    }
}

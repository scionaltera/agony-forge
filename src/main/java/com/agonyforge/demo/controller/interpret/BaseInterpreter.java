package com.agonyforge.demo.controller.interpret;

import com.agonyforge.demo.controller.Input;
import com.agonyforge.demo.controller.Output;
import com.agonyforge.demo.model.Connection;
import com.agonyforge.demo.model.Creature;
import com.agonyforge.demo.repository.CreatureRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;

public abstract class BaseInterpreter implements Interpreter {
    private CreatureRepository creatureRepository;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public abstract Output interpret(Input input, Connection connection);

    @Override
    public abstract Output prompt(Connection connection);

    public BaseInterpreter(CreatureRepository creatureRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.creatureRepository = creatureRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void echo(Creature target, Output message) {
        if (target.getConnection() == null || target.getConnection().getSessionUsername() == null) {
            return;
        }

        message.append(prompt(target.getConnection()));

        simpMessagingTemplate.convertAndSendToUser(
            target.getConnection().getSessionUsername(),
            "/queue/output",
            message);
    }

    public void echoToWorld(Output message, Creature ... exclude) {
        List<Creature> excludeList = Arrays.asList(exclude);

        creatureRepository.findByConnectionIsNotNull()
            .filter(target -> target.getConnection().getSessionUsername() != null)
            .filter(target -> !excludeList.contains(target))
            .forEach(target -> simpMessagingTemplate.convertAndSendToUser(
                target.getConnection().getSessionUsername(),
                "/queue/output",
                new Output(message, prompt(target.getConnection()))));
    }
}

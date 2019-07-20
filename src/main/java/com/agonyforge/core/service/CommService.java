package com.agonyforge.core.service;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.repository.CreatureRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Component
public class CommService {
    private CreatureRepository creatureRepository;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Inject
    public CommService(
        CreatureRepository creatureRepository,
        SimpMessagingTemplate simpMessagingTemplate) {

        this.creatureRepository = creatureRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void echo(Creature target, Interpreter interpreter, Output message) {
        if (target.getConnection() == null || target.getConnection().getSessionUsername() == null) {
            return;
        }

        message.append(interpreter.prompt(target.getConnection()));

        simpMessagingTemplate.convertAndSendToUser(
            target.getConnection().getSessionUsername(),
            "/queue/output",
            message);
    }

    public void echoToWorld(Output message, Interpreter interpreter, Creature ... exclude) {
        List<Creature> excludeList = Arrays.asList(exclude);

        creatureRepository.findByConnectionIsNotNull()
            .filter(target -> target.getConnection().getSessionUsername() != null)
            .filter(target -> !excludeList.contains(target))
            .forEach(target -> simpMessagingTemplate.convertAndSendToUser(
                target.getConnection().getSessionUsername(),
                "/queue/output",
                new Output(message, interpreter.prompt(target.getConnection()))));
    }
}

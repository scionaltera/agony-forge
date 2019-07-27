package com.agonyforge.core.controller;

import com.agonyforge.core.controller.interpret.PrimaryInterpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.repository.ConnectionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.agonyforge.core.controller.ControllerConstants.AGONY_REMOTE_IP_KEY;

@Component
public class SessionDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionDisconnectListener.class);

    private ConnectionRepository connectionRepository;
    private CreatureRepository creatureRepository;
    private PrimaryInterpreter interpreter;
    private CommService commService;

    @Inject
    public SessionDisconnectListener(
        ConnectionRepository connectionRepository,
        CreatureRepository creatureRepository,
        PrimaryInterpreter interpreter,
        CommService commService) {

        this.connectionRepository = connectionRepository;
        this.creatureRepository = creatureRepository;
        this.interpreter = interpreter;
        this.commService = commService;
    }

    @Transactional
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();

        if (attributes == null) {
            LOGGER.error("Unable to fetch session attributes from message!");
            return;
        }

        LOGGER.info("Lost connection {}@{}", event.getSessionId(), attributes.get(AGONY_REMOTE_IP_KEY));

        List<Connection> updated = new ArrayList<>();

        connectionRepository
            .findBySessionId(event.getSessionId())
            .ifPresent(connection -> {
                connection.setDisconnected(new Date());
                updated.add(connection);

                creatureRepository
                    .findByConnection(connection)
                    .ifPresent(creature -> commService.echoToWorld(new Output("[yellow]" + creature.getName() + " has disconnected."), interpreter, creature));
            });

        if (!updated.isEmpty()) {
            connectionRepository.saveAll(updated);
        }
    }
}

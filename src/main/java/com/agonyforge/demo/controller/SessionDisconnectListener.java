package com.agonyforge.demo.controller;

import com.agonyforge.demo.model.Connection;
import com.agonyforge.demo.repository.ConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.agonyforge.demo.controller.ControllerConstants.*;
import static com.agonyforge.demo.controller.ControllerConstants.AGONY_REMOTE_IP_KEY;

@Component
public class SessionDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionDisconnectListener.class);

    private ConnectionRepository connectionRepository;

    @Inject
    public SessionDisconnectListener(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();

        if (attributes != null) {
            LOGGER.info("Lost connection from {}", attributes.get(AGONY_REMOTE_IP_KEY));

            List<Connection> updated = new ArrayList<>();

            connectionRepository
                .findBySessionId(event.getSessionId())
                .ifPresent(connection -> {
                    connection.setDisconnected(new Date());
                    updated.add(connection);
                });

            if (!updated.isEmpty()) {
                connectionRepository.saveAll(updated);
            }

            return;
        }

        LOGGER.error("Unable to fetch session attributes from message!");
    }
}

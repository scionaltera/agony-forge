package com.agonyforge.core.controller;

import com.agonyforge.core.controller.greeting.GreetingLoader;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.PrimaryConnectionState;
import com.agonyforge.core.repository.ConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.agonyforge.core.controller.ControllerConstants.AGONY_CONNECTION_ID_KEY;
import static com.agonyforge.core.controller.ControllerConstants.AGONY_REMOTE_IP_KEY;
import static com.agonyforge.core.model.DefaultLoginConnectionState.RECONNECT;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

@Controller
public class WebSocketController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    private List<String> greeting;
    private ConnectionRepository connectionRepository;
    private SessionRepository sessionRepository;
    private Interpreter interpreter;

    @Inject
    public WebSocketController(
        GreetingLoader greetingLoader,
        ConnectionRepository connectionRepository,
        SessionRepository sessionRepository,
        Interpreter interpreter) {

        greeting = greetingLoader.load();
        this.connectionRepository = connectionRepository;
        this.sessionRepository = sessionRepository;
        this.interpreter = interpreter;
    }

    @Transactional
    @SubscribeMapping("/queue/output")
    public Output onSubscribe(Principal principal, Message <byte[]> message) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(message);
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
        Connection connection = new Connection();

        if (attributes != null) {
            connection.setSessionUsername(principal.getName());
            connection.setSessionId(headerAccessor.getSessionId());
            connection.setHttpSessionId((String) attributes.get(HTTP_SESSION_ID_ATTR_NAME));
            connection.setRemoteAddress((String) attributes.get(AGONY_REMOTE_IP_KEY));
            connection.setPrimaryState(PrimaryConnectionState.LOGIN);

            Session session = sessionRepository.findById(connection.getHttpSessionId());
            SecurityContext securityContext = session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);

            if (securityContext != null) {
                Authentication authentication = securityContext.getAuthentication();

                if (authentication != null && authentication.isAuthenticated()) {
                    connection.setName(authentication.getName());
                    connection.setSecondaryState(RECONNECT.name());
                }
            }

            Connection saved = connectionRepository.save(connection);

            attributes.put(AGONY_CONNECTION_ID_KEY, saved.getId());

            LOGGER.info("New connection from {}", attributes.get(AGONY_REMOTE_IP_KEY));

            return new Output(greeting).append(interpreter.prompt(connection));
        }

        LOGGER.error("Unable to get session attributes!");
        return new Output("[red]Something went wrong! The error has been logged.");
    }

    @Transactional
    @MessageMapping("/input")
    @SendToUser(value = "/queue/output", broadcast = false)
    public Output onInput(Input input, Message<byte[]> message) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(message);
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();

        if (attributes != null) {
            UUID connectionId = (UUID) attributes.get(AGONY_CONNECTION_ID_KEY);
            Connection connection = connectionRepository
                .findById(connectionId)
                .orElseThrow(() -> new NullPointerException("Unable to fetch Connection by ID: " + connectionId));

            return interpreter.interpret(input, connection);
        }

        LOGGER.error("Unable to get session attributes!");
        return new Output("[red]Something went wrong! The error has been logged.");
    }
}

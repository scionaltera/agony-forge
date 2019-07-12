package com.agonyforge.demo.controller;

import com.agonyforge.demo.model.Connection;
import com.agonyforge.demo.repository.ConnectionRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.demo.controller.ControllerConstants.*;
import static com.agonyforge.demo.controller.ControllerConstants.AGONY_REMOTE_IP_KEY;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

public class SessionDisconnectListenerTest {
    @Mock
    private ConnectionRepository connectionRepository;

    @Captor
    private ArgumentCaptor<List<Connection>> connectionListCaptor;

    private SessionDisconnectListener listener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Connection connection = new Connection();

        when(connectionRepository
            .findBySessionId(any()))
            .thenReturn(Optional.of(connection));

        listener = new SessionDisconnectListener(connectionRepository);
    }

    @Test
    public void testOnApplicationEvent() {
        Message<byte[]> message = buildMockMessage(true);
        SessionDisconnectEvent event = new SessionDisconnectEvent("source", message, "ffff", CloseStatus.NORMAL);

        listener.onApplicationEvent(event);

        verify(connectionRepository).saveAll(connectionListCaptor.capture());

        List<Connection> updated = connectionListCaptor.getValue();

        updated.forEach(connection -> assertNotNull(connection.getDisconnected()));
    }

    @Test
    public void testOnApplicationEventNoSessionAttributes() {
        Message<byte[]> message = buildMockMessage(false);
        SessionDisconnectEvent event = new SessionDisconnectEvent("source", message, "ffff", CloseStatus.NORMAL);

        listener.onApplicationEvent(event);

        verify(connectionRepository, never()).saveAll(anyList());
    }

    @Test
    public void testOnApplicationEventNoCreature() {
        Message<byte[]> message = buildMockMessage(true);
        SessionDisconnectEvent event = new SessionDisconnectEvent("source", message, "ffff", CloseStatus.NORMAL);

        when(connectionRepository
            .findBySessionId(anyString()))
            .thenReturn(Optional.empty());

        listener.onApplicationEvent(event);

        verify(connectionRepository, never()).saveAll(anyList());
    }

    private Message<byte[]> buildMockMessage(boolean includeAttributes) {
        UUID springSessionId = UUID.randomUUID();
        UUID stompSessionId = UUID.randomUUID();
        Map<String, Object> sessionAttributes = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();

        sessionAttributes.put(HTTP_SESSION_ID_ATTR_NAME, springSessionId.toString());
        sessionAttributes.put(AGONY_REMOTE_IP_KEY, "12.34.56.78");

        headers.put(SimpMessageHeaderAccessor.SESSION_ID_HEADER, stompSessionId.toString());

        if (includeAttributes) {
            headers.put(SimpMessageHeaderAccessor.SESSION_ATTRIBUTES, sessionAttributes);
        }

        return new GenericMessage<>(new byte[0], headers);
    }
}

package com.agonyforge.core.controller;

import com.agonyforge.core.controller.interpret.PrimaryInterpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.repository.ConnectionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static com.agonyforge.core.controller.ControllerConstants.AGONY_REMOTE_IP_KEY;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

public class SessionDisconnectListenerTest {
    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private PrimaryInterpreter interpreter;

    @Mock
    private CommService commService;

    @Captor
    private ArgumentCaptor<List<Connection>> connectionListCaptor;

    private SessionDisconnectListener listener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Connection connection = new Connection();
        Creature creature = new Creature();

        when(connectionRepository
            .findBySessionId(any()))
            .thenReturn(Optional.of(connection));

        when(creatureRepository
            .findByConnection(any()))
            .thenReturn(Optional.of(creature));

        listener = new SessionDisconnectListener(
            connectionRepository,
            creatureRepository,
            interpreter,
            commService);
    }

    @Test
    public void testOnApplicationEvent() {
        Message<byte[]> message = buildMockMessage(true);
        SessionDisconnectEvent event = new SessionDisconnectEvent("source", message, "ffff", CloseStatus.NORMAL);

        listener.onApplicationEvent(event);

        verify(commService).echoToWorld(any(), eq(interpreter), any());
        verify(connectionRepository).saveAll(connectionListCaptor.capture());

        List<Connection> updated = connectionListCaptor.getValue();

        updated.forEach(connection -> assertNotNull(connection.getDisconnected()));
    }

    @Test
    public void testOnApplicationEventNoSessionAttributes() {
        Message<byte[]> message = buildMockMessage(false);
        SessionDisconnectEvent event = new SessionDisconnectEvent("source", message, "ffff", CloseStatus.NORMAL);

        listener.onApplicationEvent(event);

        verify(commService, never()).echoToWorld(any(), eq(interpreter), any());
        verify(connectionRepository, never()).saveAll(anyList());
    }

    @Test
    public void testOnApplicationEventNoConnection() {
        Message<byte[]> message = buildMockMessage(true);
        SessionDisconnectEvent event = new SessionDisconnectEvent("source", message, "ffff", CloseStatus.NORMAL);

        when(connectionRepository
            .findBySessionId(anyString()))
            .thenReturn(Optional.empty());

        listener.onApplicationEvent(event);

        verify(commService, never()).echoToWorld(any(), eq(interpreter), any());
        verify(connectionRepository, never()).saveAll(anyList());
    }

    @Test
    public void testOnApplicationEventNoCreature() {
        Message<byte[]> message = buildMockMessage(true);
        SessionDisconnectEvent event = new SessionDisconnectEvent("source", message, "ffff", CloseStatus.NORMAL);

        when(creatureRepository
            .findByConnection(any()))
            .thenReturn(Optional.empty());

        listener.onApplicationEvent(event);

        verify(commService, never()).echoToWorld(any(), eq(interpreter), any());
        verify(connectionRepository).saveAll(anyList());
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

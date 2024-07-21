package org.Jerry.websockettrialrun.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Jerry.websockettrialrun.model.ChatMessage;
import org.Jerry.websockettrialrun.model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    // TODO -- Rational for: Synchronized on Connection

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessageSendingOperations msgTemplate;
    private Map<String, String> sessionIdToUser = new HashMap<>();
    private final Object lock = new Object();

    public void handleWebSocketDisconnectV2(WebSocketSession session) {}

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        synchronized (lock) {
            // TODO -- Implement removal from list
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            String username = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username").toString();
            if(username != null) {
                LOGGER.info("User disconnected: {}", username);
                var chatMessage = ChatMessage
                        .builder()
                        .type(MessageType.DISCONNECTED)
                        .sender(username)
                        .build();
                msgTemplate.convertAndSend("/topic/public", chatMessage);
            }
        }
    }

    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event) {
        synchronized (lock) {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            // Extract any headers or parameters you might need
            String sessionId = headerAccessor.getSessionId();
            LOGGER.info("New WebSocket connection attempt. Session ID: {}", sessionId);

            String username = headerAccessor.getFirstNativeHeader("username");
            // TODO -- Implement a block just incase the username already exists.
            if (username != null) {
                Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", username);
                sessionIdToUser.put(sessionId, username);
                LOGGER.info("Username {} added to session attributes", username);
                var chatMessage = ChatMessage
                        .builder()
                        .type(MessageType.CONNECT)
                        .sender(username)
                        .build();
                msgTemplate.convertAndSend("/topic/public", chatMessage);
            }
            // Additional actions on connection attempt can be added here, e.g., validating headers
        }
    }

    @EventListener
    public void handleWebSocketConnected(SessionConnectedEvent event) {
        synchronized (lock) {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            String sessionId = headerAccessor.getSessionId();
            LOGGER.info("WebSocket connection established for User {}. Session ID: {}", sessionIdToUser.get(sessionId), sessionId);
            String username = sessionIdToUser.get(sessionId);
            if(username != null) {
                LOGGER.info("User Connected: {}", username);
                var chatMessage = ChatMessage
                        .builder()
                        .type(MessageType.CONNECTED)
                        .sender(username)
                        .build();
                msgTemplate.convertAndSend("/topic/public", chatMessage);
            }
            // Additional actions on new connection can be added here, e.g., sending a welcome message
        }
    }
}

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

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessageSendingOperations msgTemplate;
    private static int counter = 1;

    public void handleWebSocketDisconnectV2(WebSocketSession session) {}

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getSessionAttributes().get("username").toString();
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

    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // Extract any headers or parameters you might need
        String sessionId = headerAccessor.getSessionId();
        LOGGER.info("New WebSocket connection attempt. Session ID: {}", sessionId);

        // Additional actions on connection attempt can be added here, e.g., validating headers
    }

    @EventListener
    public void handleWebSocketConnected(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        LOGGER.info("Received a new web socket connection. Session ID: {}", sessionId);

        // Additional actions on new connection can be added here, e.g., sending a welcome message
    }
}

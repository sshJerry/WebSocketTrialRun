package org.Jerry.websockettrialrun.chat;

import org.Jerry.websockettrialrun.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {return chatMessage;}

    @MessageMapping("/chat.userConnect")
    @SendTo("/topic/public")
    public ChatMessage userConnect(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        return chatMessage;
    }

    @MessageMapping("/chat.userConnected")
    @SendTo("/topic/public")
    public ChatMessage userConnected(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        //headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        String username = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username").toString();
        if (username != null) chatMessage.setSender(username);
        return chatMessage;
    }

    @MessageMapping("/chat.userDisconnected")
    @SendTo("/topic/public")
    public ChatMessage userDisconnected(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username").toString();
        if (username != null) chatMessage.setSender(username);
        return chatMessage;
    }
}

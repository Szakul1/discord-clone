package com.example.discord.controller;

import com.example.discord.dto.MessageDto;
import com.example.discord.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final MessageService messageService;

    @MessageMapping("/create/{textChannelId}")
    @SendTo("/start/create/{textChannelId}")
    public MessageDto create(@DestinationVariable Long textChannelId, @Payload String message, Principal principal) {
        return messageService.create(textChannelId, message, principal);
    }

    @MessageMapping("/update/{textChannelId}")
    @SendTo("/start/update/{textChannelId}")
    public MessageDto update(@DestinationVariable Long textChannelId, @Payload MessageDto message, Principal principal) {
        return messageService.update(textChannelId, message, principal);
    }

    @MessageMapping("/delete/{textChannelId}")
    @SendTo("/start/delete/{textChannelId}")
    public Long delete(@DestinationVariable Long textChannelId, @Payload Long messageId, Principal principal) {
        return messageService.delete(textChannelId, messageId, principal);
    }

}

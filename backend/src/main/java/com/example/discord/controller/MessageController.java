package com.example.discord.controller;

import com.example.discord.dto.MessageDto;
import com.example.discord.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{channelId}")
    public List<MessageDto> getChannelMessages(@PathVariable Long channelId, @RequestParam Integer page) {
        return messageService.getMessages(channelId, page);
    }

}

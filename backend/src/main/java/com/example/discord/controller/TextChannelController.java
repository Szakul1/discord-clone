package com.example.discord.controller;

import com.example.discord.dto.TextChannelDto;
import com.example.discord.service.TextChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/textChannel")
@RequiredArgsConstructor
public class TextChannelController {

    private final TextChannelService textChannelService;

    @GetMapping("/{serverId}")
    public List<TextChannelDto> getChannels(@PathVariable Long serverId) {
        return textChannelService.getChannels(serverId);
    }

    @PostMapping("/{serverId}")
    public TextChannelDto create(@PathVariable Long serverId, @RequestBody TextChannelDto textChannelDto) {
        return textChannelService.create(serverId, textChannelDto);
    }

    @PutMapping("/{textChannelId}")
    public TextChannelDto update(@PathVariable Long textChannelId, @RequestBody TextChannelDto textChannelDto) {
        return textChannelService.update(textChannelId, textChannelDto);
    }

    @DeleteMapping("/{textChannelId}")
    public void delete(@PathVariable Long textChannelId) {
        textChannelService.delete(textChannelId);
    }

}

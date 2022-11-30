package com.example.discord.service;

import com.example.discord.entity.DiscordUser;
import com.example.discord.entity.Server;
import com.example.discord.entity.TextChannel;
import com.example.discord.dto.TextChannelDto;
import com.example.discord.exception.DiscordException;
import com.example.discord.mapper.TextChannelMapper;
import com.example.discord.repository.ServerRepository;
import com.example.discord.repository.TextChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TextChannelService {

    private final TextChannelMapper textChannelMapper;

    private final ServerRepository serverRepository;
    private final TextChannelRepository textChannelRepository;

    private final ServerService serverService;

    public List<TextChannelDto> getChannels(Long serverId) {
        Server server = serverService.getServer(serverId);
        serverService.authorizeUser(server);

        return server.getTextChannels().stream()
                .map(textChannelMapper::toDto)
                .collect(Collectors.toList());
    }

    public TextChannelDto create(Long serverId, TextChannelDto textChannelDto) {
        Server server = serverService.getServer(serverId);
        serverService.isOwner(server);

        TextChannel textChannel = TextChannel.builder()
                .name(textChannelDto.getName().trim())
                .build();
        server.getTextChannels().add(textChannel);
        serverRepository.save(server);
        return textChannelMapper.toDto(server.getTextChannels().get(server.getTextChannels().size() - 1));
    }

    public TextChannelDto update(Long textChannelId, TextChannelDto textChannelDto) {
        serverService.isOwner(serverService.getServerByChannelId(textChannelId));

        TextChannel textChannel = textChannelRepository.findById(textChannelId)
                .orElseThrow(() -> new DiscordException("TextChannel with id: " + textChannelId + " not found"));
        textChannel.setName(textChannelDto.getName().trim());
        return textChannelMapper.toDto(textChannelRepository.save(textChannel));
    }

    public void delete(Long textChannelId) {
        serverService.isOwner(serverService.getServerByChannelId(textChannelId));
        textChannelRepository.deleteById(textChannelId);
    }

}

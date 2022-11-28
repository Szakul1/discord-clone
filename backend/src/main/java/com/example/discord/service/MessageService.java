package com.example.discord.service;

import com.example.discord.dto.MessageDto;
import com.example.discord.entity.DiscordUser;
import com.example.discord.entity.Message;
import com.example.discord.entity.TextChannel;
import com.example.discord.exception.DiscordException;
import com.example.discord.mapper.MessageMapper;
import com.example.discord.repository.MessageRepository;
import com.example.discord.repository.TextChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final TextChannelRepository textChannelRepository;
    private final MessageRepository messageRepository;

    private final ServerService serverService;
    private final DiscordUserService discordUserService;

    private final MessageMapper messageMapper;

    public List<MessageDto> getMessages(Long textChannelId, Integer page) {
        serverService.authorizeUser(textChannelId);
        page = (int) Math.ceil(messageRepository.countMessages(textChannelId) / 10.0) - 1 - page;
        if (page < 0)
            return List.of();
        return messageRepository.getChannelMessages(textChannelId,
                         PageRequest.of(page, 10)).stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDto create(Long textChannelId, String text,  Principal principal) {
        serverService.authorizeUser(textChannelId, principal.getName());
        DiscordUser author = discordUserService.getUser(principal.getName());

        TextChannel textChannel = textChannelRepository.findById(textChannelId)
                .orElseThrow(() -> new DiscordException("Text channel with id: " + textChannelId + " not found"));


        Message message = Message.builder()
                .message(text.trim())
                .author(author)
                .build();
        Message saved = messageRepository.save(message);

        textChannel.getMessages().add(saved);
        textChannelRepository.save(textChannel);
        return messageMapper.toDto(saved);
    }

    public MessageDto update(Long textChannelId, MessageDto messageDto, Principal principal) {
        serverService.authorizeUser(textChannelId, principal.getName());

        Message message = checkAuthor(messageDto.getId(), principal.getName());

        message.setMessage(messageDto.getMessage().trim());
        message.setEdited(true);
        return messageMapper.toDto(messageRepository.save(message));
    }

    public Long delete(Long textChannelId, Long messageId, Principal principal) {
        serverService.authorizeUser(textChannelId, principal.getName());
        Message message = checkAuthor(messageId, principal.getName());
        messageRepository.delete(message);
        return messageId;
    }

    private Message checkAuthor(Long messageId, String author) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new DiscordException("Message with id: " + messageId + " not found"));

        if (!message.getAuthor().getUsername().equals(author)) {
            throw new DiscordException("User is not message author");
        }

        return message;
    }

    @Deprecated
    private List<MessageDto> getMessagesAlternative(Long textChannelId, Integer page) {
        List<MessageDto> messages = messageRepository.getChannelMessagesReverse(textChannelId, PageRequest.of(page, 10)).stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
        Collections.reverse(messages);
        return messages;
    }

}

package com.example.discord.config;

import com.example.discord.dto.CreateUserDto;
import com.example.discord.dto.DiscordUserDto;
import com.example.discord.service.DiscordUserService;
import com.example.discord.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {
    private static final String USERNAME_HEADER = "username";
    private static final String PASSWORD_HEADER = "password";

    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String username = accessor.getFirstNativeHeader(USERNAME_HEADER);
            String password = accessor.getFirstNativeHeader(PASSWORD_HEADER);

            UsernamePasswordAuthenticationToken user = userService.authenticate(username, password);
            accessor.setUser(user);
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

}

package com.example.discord.service;

import com.example.discord.entity.DiscordUser;
import com.example.discord.exception.DiscordException;
import com.example.discord.repository.DiscordUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final DiscordUserRepository discordUserRepository;

    public Authentication getUserAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public DiscordUser getCurrentUser() {
        String username = getUserAuth().getName();
        return discordUserRepository.findByUsername(username)
                .orElseThrow(() -> new DiscordException("DiscordUser with username: " + username + " not found"));
    }

    public String getUsername() {
        return getUserAuth().getName();
    }

    public UsernamePasswordAuthenticationToken authenticate(String username, String password) {
        DiscordUser user = discordUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationCredentialsNotFoundException("Password not found");
        }

        return new UsernamePasswordAuthenticationToken(username, null, List.of());
    }

}

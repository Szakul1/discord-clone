package com.example.discord.service;

import com.example.discord.entity.DiscordUser;
import com.example.discord.exception.DiscordException;
import com.example.discord.exception.StatusCode;
import com.example.discord.repository.DiscordUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final DiscordUserRepository discordUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DiscordUser discordUser = discordUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("DiscordUser: " + username + " not found"));

        return User.builder()
                .username(discordUser.getUsername())
                .password(discordUser.getPassword())
                .disabled(!discordUser.isEnabled())
                .authorities(buildSimpleGrantedAuthorities())
                .build();
    }

    private List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities() {
        return List.of();
    }

}

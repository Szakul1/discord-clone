package com.example.discord.controller;

import com.example.discord.dto.CreateUserDto;
import com.example.discord.dto.DiscordUserDto;
import com.example.discord.service.DiscordUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final DiscordUserService discordUserService;

    @PostMapping("/auth")
    public DiscordUserDto authenticate(@RequestBody CreateUserDto userDto) {
        return discordUserService.authenticate(userDto);
    }

}

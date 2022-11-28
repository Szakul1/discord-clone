package com.example.discord.controller;

import com.example.discord.dto.CreateUserDto;
import com.example.discord.dto.DiscordUserDto;
import com.example.discord.service.DiscordUserService;
import com.example.discord.service.FileService;
import com.example.discord.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class DiscordUserController {

    private final DiscordUserService discordUserService;

    @GetMapping("/{serverId}")
    public List<DiscordUserDto> getUsers(@PathVariable Long serverId) {
        return discordUserService.getUsers(serverId);
    }

    @PostMapping("/register")
    public DiscordUserDto create(@RequestBody @Valid CreateUserDto createUserDto) {
        return discordUserService.create(createUserDto);
    }

    @GetMapping("/confirm")
    public void confirmEmail(@RequestParam("token") String token) {
        discordUserService.confirmEmail(token);
    }

    @PutMapping("/resend")
    public void resendToken(@RequestBody CreateUserDto userDto) {
        discordUserService.resendToken(userDto);
    }

    @PutMapping("/resendEmail")
    public void resendEmail(@RequestParam("email") String email) {
        discordUserService.resendEmail(email);
    }

    @PutMapping("/username")
    public DiscordUserDto updateUsername(@RequestBody CreateUserDto userDto) {
        return discordUserService.updateUsername(userDto);
    }

    @PutMapping("/email")
    public DiscordUserDto updateEmail(@RequestBody CreateUserDto userDto) {
        return discordUserService.updateEmail(userDto);
    }

    @PutMapping("/password")
    public DiscordUserDto updatePassword(@RequestBody @Valid CreateUserDto userDto) {
        return discordUserService.updatePassword(userDto);
    }

    @PostMapping("/avatar")
    public DiscordUserDto uploadAvatar(@RequestPart(value = "avatar") MultipartFile avatar) throws Exception {
        return discordUserService.uploadAvatar(avatar);
    }

    @GetMapping("/avatar/{username}")
    public byte[] getAvatar(@PathVariable String username) throws Exception {
        return discordUserService.getAvatar(username);
    }

}

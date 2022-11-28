package com.example.discord.dto;

import lombok.Data;

@Data
public class DiscordUserDto {

    private Long id;

    private String username;

    private String email;

    private byte[] avatar;

    private boolean avatarSet;

}

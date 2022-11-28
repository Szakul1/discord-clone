package com.example.discord.dto;

import lombok.Data;

import java.util.List;

@Data
public class ServerDto {

    private Long id;

    private String name;

    private String owner;

    private byte[] logo;

    private String inviteToken;

}

package com.example.discord.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {

    private Long id;

    private String message;

    private String author;

    private LocalDateTime creationDate;

    private boolean edited;

}

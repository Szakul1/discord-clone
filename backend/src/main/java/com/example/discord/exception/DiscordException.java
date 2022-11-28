package com.example.discord.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class DiscordException extends RuntimeException {

    StatusCode status;

    public DiscordException(String message) {
        super(message);
    }

    public DiscordException(String message, StatusCode status) {
        super(message);
        this.status = status;
    }

}

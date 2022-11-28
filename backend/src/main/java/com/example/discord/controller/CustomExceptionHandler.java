package com.example.discord.controller;

import com.example.discord.exception.DiscordException;
import com.example.discord.exception.StatusCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DiscordException.class)
    public ResponseEntity<StatusCode> handleDiscordException(DiscordException exception, WebRequest request) {
        exception.printStackTrace();
        return new ResponseEntity<>(exception.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

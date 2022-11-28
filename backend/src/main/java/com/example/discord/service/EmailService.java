package com.example.discord.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${frontend-url}")
    private String serverUrl;

    private final JavaMailSender mailSender;

    public void sendMail(String address, String token) {
        String sender = "Discord";
        String subject = "Registration Confirmation";
        String confirmationUrl = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/login")
                .queryParam("token", token)
                .toUriString();
        String message = "Confirm your email address\n" + confirmationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(address);
        email.setSubject(subject);
        email.setFrom(sender);
        email.setText(message);
        mailSender.send(email);
    }

}

package com.example.discord.service;

import com.example.discord.entity.DiscordUser;
import com.example.discord.entity.VerificationToken;
import com.example.discord.exception.DiscordException;
import com.example.discord.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private static final int EXPIRATION = 60 * 24;

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationToken createToken(DiscordUser discordUser) {
        VerificationToken token = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .expireDate(LocalDateTime.now().plusMinutes(EXPIRATION))
                .discordUser(discordUser)
                .build();
        return verificationTokenRepository.save(token);
    }

    public VerificationToken getToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new DiscordException("Token doesnt exist"));
    }

    public void remove(VerificationToken verificationToken) {
        verificationTokenRepository.delete(verificationToken);
    }

    public VerificationToken resendToken(DiscordUser discordUser) {
        VerificationToken verificationToken = verificationTokenRepository.findByDiscordUser(discordUser)
                .orElseThrow(() -> new DiscordException("User: " + discordUser.getUsername() + " doesnt have verification token"));

        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpireDate(LocalDateTime.now().plusMinutes(EXPIRATION));

        return verificationTokenRepository.save(verificationToken);
    }

}

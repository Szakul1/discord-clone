package com.example.discord.repository;

import com.example.discord.entity.DiscordUser;
import com.example.discord.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByDiscordUser(DiscordUser discordUser);

}

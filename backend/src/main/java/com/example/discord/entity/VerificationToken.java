package com.example.discord.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String token;

    private LocalDateTime expireDate = LocalDateTime.now().plusMinutes(EXPIRATION);

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "discord_user_id")
    private DiscordUser discordUser;

}

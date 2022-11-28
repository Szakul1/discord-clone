package com.example.discord.repository;

import com.example.discord.entity.Server;
import com.example.discord.entity.TextChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {

    Optional<Server> findByTextChannelsId(Long textChannelId);

    boolean existsByIdAndDiscordUsersUsername(Long id, String username);

    boolean existsByIdAndBannedUsersUsername(Long id, String username);

    Optional<Server> findByInviteToken(String token);

}

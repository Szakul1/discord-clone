package com.example.discord.repository;

import com.example.discord.entity.TextChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextChannelRepository extends JpaRepository<TextChannel, Long> {
}

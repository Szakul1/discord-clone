package com.example.discord.repository;

import com.example.discord.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from TextChannel t inner join t.messages m where t.id = :channelId order by m.id desc")
    List<Message> getChannelMessagesReverse(@Param("channelId") Long textChannelId, Pageable pageable);

    @Query("select size(t.messages) as s from TextChannel t where t.id = :channelId")
    Integer countMessages(@Param("channelId") Long textChannelId);

    @Query("select t.messages from TextChannel t where t.id = :channelId")
    List<Message> getChannelMessages(@Param("channelId") Long textChannelId, Pageable pageable);

}

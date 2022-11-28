package com.example.discord.mapper;

import com.example.discord.dto.MessageDto;
import com.example.discord.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "author", source = "author.username")
    MessageDto toDto(Message message);

}

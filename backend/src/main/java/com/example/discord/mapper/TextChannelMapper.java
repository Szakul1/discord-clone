package com.example.discord.mapper;

import com.example.discord.entity.TextChannel;
import com.example.discord.dto.TextChannelDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TextChannelMapper {

    TextChannelDto toDto(TextChannel textChannel);

    TextChannel toEntity(TextChannelDto textChannelDto);

}

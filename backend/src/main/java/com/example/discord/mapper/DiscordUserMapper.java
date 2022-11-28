package com.example.discord.mapper;

import com.example.discord.dto.DiscordUserDto;
import com.example.discord.dto.ServerDto;
import com.example.discord.entity.DiscordUser;
import com.example.discord.entity.Server;
import com.example.discord.service.DiscordUserService;
import com.example.discord.service.FileService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class DiscordUserMapper {

    @Autowired
    private FileService fileService;

    public abstract DiscordUserDto toDto(DiscordUser discordUser);

    public abstract DiscordUser toEntity(DiscordUserDto discordUserDto);

    @AfterMapping
    public void mapAvatar(DiscordUser discordUser, @MappingTarget DiscordUserDto discordUserDto) {
        try {
            discordUserDto.setAvatar(discordUser.getAvatarLocation() != null ?
                    fileService.getAvatar(discordUser.getAvatarLocation(), 100) :
                    fileService.getDefaultAvatar(100));
            discordUserDto.setAvatarSet(discordUser.getAvatarLocation() != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

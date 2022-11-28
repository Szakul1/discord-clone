package com.example.discord.mapper;

import com.example.discord.dto.ServerDto;
import com.example.discord.entity.Server;
import com.example.discord.service.DiscordUserService;
import com.example.discord.service.FileService;
import com.example.discord.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ServerMapper {

    @Autowired
    private FileService fileService;

    @Mapping(target = "owner", source = "server.owner.username")
    public abstract ServerDto toDto(Server server, int size);

    @Mapping(target = "owner", source = "server.owner.username")
    public abstract ServerDto toDto(Server server);

    @AfterMapping
    public void mapLogo(Server server, @MappingTarget ServerDto serverDto, int size) {
        if (server.getLogoLocation() != null) {
            try {
                serverDto.setLogo(fileService.getLogo(server.getLogoLocation(), size));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @AfterMapping
    public void mapLogoFullSize(Server server, @MappingTarget ServerDto serverDto) {
        if (server.getLogoLocation() != null) {
            try {
                serverDto.setLogo(fileService.getLogo(server.getLogoLocation()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}

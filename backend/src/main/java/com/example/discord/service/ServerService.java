package com.example.discord.service;

import com.example.discord.dto.DiscordUserDto;
import com.example.discord.dto.ServerDto;
import com.example.discord.entity.DiscordUser;
import com.example.discord.entity.Server;
import com.example.discord.entity.TextChannel;
import com.example.discord.exception.DiscordException;
import com.example.discord.mapper.DiscordUserMapper;
import com.example.discord.mapper.ServerMapper;
import com.example.discord.repository.DiscordUserRepository;
import com.example.discord.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerService {

    public static final String DEFAULT_CHANNEL_NAME = "General";
    public static final int DEFAULT_LOGO_SIZE = 100;
    private final ServerMapper serverMapper;
    private DiscordUserMapper discordUserMapper;

    private final ServerRepository serverRepository;
    private final DiscordUserRepository discordUserRepository;

    private final UserService userService;
    private final FileService fileService;

    public void join(String token) {
        Server server = findByToken(token);
        DiscordUser currentUser = userService.getCurrentUser();

        if (serverRepository.existsByIdAndBannedUsersUsername(server.getId(), currentUser.getUsername())) {
            throw new DiscordException("User banned from this server");
        }

        if (serverRepository.existsByIdAndDiscordUsersUsername(server.getId(), currentUser.getUsername())) {
            return;
        }

        server.getDiscordUsers().add(currentUser);
        serverRepository.save(server);
    }

    public ServerDto getServer(String token) {
        return serverMapper.toDto(findByToken(token));
    }

    public List<ServerDto> getUserServers() {
        return userService.getCurrentUser()
                .getServers()
                .stream()
                .map(server -> serverMapper.toDto(server, DEFAULT_LOGO_SIZE))
                .collect(Collectors.toList());
    }

    public ServerDto create(MultipartFile logo, ServerDto serverDto) throws Exception {
        DiscordUser currentUser = userService.getCurrentUser();

        TextChannel textChannel = new TextChannel();
        textChannel.setName(DEFAULT_CHANNEL_NAME);

        Server server = Server.builder()
                .name(serverDto.getName().trim())
                .owner(currentUser)
                .inviteToken(UUID.randomUUID().toString())
                .discordUsers(List.of(currentUser))
                .textChannels(List.of(textChannel)).build();

        if (logo != null)
            server.setLogoLocation(getLocation(logo, serverDto));

        return serverMapper.toDto(serverRepository.save(server), DEFAULT_LOGO_SIZE);
    }

    public void delete(Long id) {
        Server server = getServer(id);
        isOwner(server);
        serverRepository.deleteById(id);
    }

    public ServerDto update(Long id, MultipartFile logo, ServerDto serverDto) throws Exception {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new DiscordException("Server with id: " + id + " not found"));
        isOwner(server);

        server.setName(serverDto.getName().trim());
        if (logo != null) {
            String location = server.getLogoLocation();
            if (location != null) {
                fileService.deleteLogo(location);
            }
            if (logo.isEmpty()) {
                server.setLogoLocation(null);
            } else {
                server.setLogoLocation(getLocation(logo, serverDto));
            }
        }

        return serverMapper.toDto(serverRepository.save(server), DEFAULT_LOGO_SIZE);
    }

    public void leaveServer(Long serverId) {
        Server server = getServer(serverId);
        if (userService.getUsername().equals(server.getOwner().getUsername())) {
            throw new DiscordException("Owner cannot leave server");
        }

        DiscordUser currentUser = userService.getCurrentUser();
        server.getDiscordUsers().remove(currentUser);
        serverRepository.save(server);
    }

    public void banUser(Long serverId, Long userId) {
        Server server = getServer(serverId);
        isOwner(server);

        DiscordUser discordUser = discordUserRepository.findById(userId)
                .orElseThrow(() -> new DiscordException("User with id: " + userId + " not found"));
        if (server.getOwner().equals(discordUser)) {
            throw new DiscordException("Cannot ban server owner");
        }

        server.getDiscordUsers().remove(discordUser);
        server.getBannedUsers().add(discordUser);
        serverRepository.save(server);
    }

    private String getLocation(MultipartFile logo, ServerDto server) throws Exception {
        fileService.checkImageExtension(logo);
        String filename = server.getName() + System.currentTimeMillis();
        fileService.uploadLogo(filename, logo);
        return filename;
    }

    public Server getServer(Long serverId) {
        return serverRepository.findById(serverId)
                .orElseThrow(() -> new DiscordException("Server with id: " + serverId + " not found"));
    }

    public void authorizeUser(Server server) {
        String username = userService.getUsername();

        if (!serverRepository.existsByIdAndDiscordUsersUsername(server.getId(), username)) {
            throw new UsernameNotFoundException("User: " + username + " not authorized");
        }
    }

    public void authorizeUser(Server server, String username) {
        if (!serverRepository.existsByIdAndDiscordUsersUsername(server.getId(), username)) {
            throw new UsernameNotFoundException("User: " + username + " not authorized");
        }
    }

    public Server getServerByChannelId(Long textChannelId) {
        return serverRepository.findByTextChannelsId(textChannelId)
                .orElseThrow(() -> new DiscordException("Server not found"));
    }

    public void isOwner(Server server) {
        if (!userService.getUsername().equals(server.getOwner().getUsername())) {
            throw new DiscordException("User is not server owner");
        }
    }

    private Server findByToken(String token) {
        return serverRepository.findByInviteToken(token)
                .orElseThrow(() -> new DiscordException("Server with token: " + token + " not found"));
    }

}

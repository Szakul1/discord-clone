package com.example.discord.controller;

import com.example.discord.dto.DiscordUserDto;
import com.example.discord.dto.ServerDto;
import com.example.discord.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/server")
public class ServerController {

    private final ServerService serverService;

    @GetMapping("/join")
    public void joinServer(@RequestParam("token") String token) {
        serverService.join(token);
    }

    @GetMapping("/get")
    public ServerDto getServerByToken(@RequestParam("token") String token) {
        return serverService.getServer(token);
    }

    @GetMapping
    public List<ServerDto> getUserServers() {
        return serverService.getUserServers();
    }

    @PostMapping
    public ServerDto create(@RequestPart(value = "logo", required = false) MultipartFile logo,
                            @RequestPart("server") ServerDto serverDto) throws Exception {
        return serverService.create(logo, serverDto);
    }

    @PutMapping("/{id}")
    public ServerDto update(@PathVariable Long id,
                            @RequestPart(value = "logo", required = false) MultipartFile logo,
                            @RequestPart("server") ServerDto serverDto) throws Exception {
        return serverService.update(id, logo, serverDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        serverService.delete(id);
    }

    @DeleteMapping("/ban/{serverId}/{userId}")
    public void banUser(@PathVariable Long serverId, @PathVariable Long userId) {
        serverService.banUser(serverId, userId);
    }

    @DeleteMapping("/leave/{serverId}")
    public void leaveServer(@PathVariable Long serverId) {
        serverService.leaveServer(serverId);
    }

}

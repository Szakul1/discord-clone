package com.example.discord.config;

import com.example.discord.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class MinioInit {

    private final FileService fileService;

    @EventListener(ContextRefreshedEvent.class)
    public void init() throws Exception {
        fileService.initStructure();
    }

}

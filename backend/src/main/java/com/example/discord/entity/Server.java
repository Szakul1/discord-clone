package com.example.discord.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @OneToOne
    private DiscordUser owner;

    private String logoLocation;

    @NotBlank
    private String inviteToken;

    @ManyToMany
    private List<DiscordUser> discordUsers;

    @ManyToMany
    private List<DiscordUser> bannedUsers;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "server_id")
    private List<TextChannel> textChannels;

}

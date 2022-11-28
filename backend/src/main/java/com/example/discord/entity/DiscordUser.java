package com.example.discord.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscordUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Length(min = 4)
    @Pattern(regexp = "\\S+")
    private String username;

    private String password;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    private String avatarLocation;

    private boolean enabled = false;

    @ManyToMany(mappedBy = "discordUsers")
    private List<Server> servers;

    @Override
    public String toString() {
        return "DiscordUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}

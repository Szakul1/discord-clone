package com.example.discord.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Data
public class CreateUserDto {

    private String username;

    private String email;

    @Pattern(regexp = "\\S+")
    @Length(min = 4)
    private String password;

}

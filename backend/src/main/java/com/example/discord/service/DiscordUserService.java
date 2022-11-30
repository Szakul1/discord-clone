package com.example.discord.service;

import com.example.discord.dto.CreateUserDto;
import com.example.discord.dto.DiscordUserDto;
import com.example.discord.entity.DiscordUser;
import com.example.discord.entity.Server;
import com.example.discord.entity.VerificationToken;
import com.example.discord.exception.DiscordException;
import com.example.discord.exception.StatusCode;
import com.example.discord.mapper.DiscordUserMapper;
import com.example.discord.repository.DiscordUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscordUserService {
    private final DiscordUserMapper discordUserMapper;

    private final DiscordUserRepository discordUserRepository;

    private final FileService fileService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final UserService userService;
    private final ServerService serverService;

    private final PasswordEncoder passwordEncoder;

    public List<DiscordUserDto> getUsers(Long serverId) {
        Server server = serverService.getServer(serverId);
        serverService.authorizeUser(server);

        return server.getDiscordUsers().stream()
                .map(discordUserMapper::toDto)
                .collect(Collectors.toList());
    }

    public DiscordUser getUser(String username) {
        return discordUserRepository.findByUsername(username)
                .orElseThrow(() -> new DiscordException("User with username: " + username +
                        " not found"));
    }

    @Transactional
    public DiscordUserDto create(CreateUserDto createUserDto) {
        validateUserDto(createUserDto);

        DiscordUser discordUser = DiscordUser.builder()
                .username(createUserDto.getUsername())
                .password(passwordEncoder.encode(createUserDto.getPassword()))
                .email(createUserDto.getEmail())
                .build();

        DiscordUser savedUser = discordUserRepository.save(discordUser);

        createEmailVerification(discordUser);

        return discordUserMapper.toDto(savedUser);
    }

    @Transactional
    public void confirmEmail(String token) {
        VerificationToken verificationToken = verificationTokenService.getToken(token);
        if (verificationToken.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new DiscordException("Token expired", StatusCode.TOKEN_EXPIRED);
        }

        DiscordUser discordUser = verificationToken.getDiscordUser();
        discordUser.setEnabled(true);
        discordUserRepository.save(discordUser);

        verificationTokenService.remove(verificationToken);
    }

    @Transactional
    public void resendToken(CreateUserDto userDto) {
        DiscordUser discordUser = checkUserCredentials(userDto);
        VerificationToken verificationToken = verificationTokenService.resendToken(discordUser);
        emailService.sendMail(discordUser.getEmail(), verificationToken.getToken());
    }

    @Transactional
    public void resendEmail(String email) {
        DiscordUser discordUser = discordUserRepository.findByEmail(email)
                .orElseThrow(() -> new DiscordException("User with email: " + email + " not found"));

        VerificationToken verificationToken = verificationTokenService.resendToken(discordUser);
        emailService.sendMail(discordUser.getEmail(), verificationToken.getToken());
    }

    public DiscordUserDto authenticate(CreateUserDto userDto) {
        DiscordUser user = checkUserCredentials(userDto);

        if (!user.isEnabled()) {
            throw new DiscordException("User not enabled", StatusCode.USER_NOT_ENABLED);
        }

        return discordUserMapper.toDto(user);
    }

    public DiscordUserDto updateUsername(CreateUserDto userDto) {
        DiscordUser currentUser = userService.getCurrentUser();
        currentUser.setUsername(userDto.getUsername());
        return discordUserMapper.toDto(discordUserRepository.save(currentUser));
    }

    public DiscordUserDto updateEmail(CreateUserDto userDto) {
        DiscordUser currentUser = userService.getCurrentUser();

        currentUser.setEmail(userDto.getEmail());
        currentUser.setEnabled(false);
        DiscordUser updated = discordUserRepository.save(currentUser);

        createEmailVerification(updated);
        return discordUserMapper.toDto(updated);
    }

    public DiscordUserDto updatePassword(CreateUserDto userDto) {
        DiscordUser currentUser = userService.getCurrentUser();
        currentUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return discordUserMapper.toDto(discordUserRepository.save(currentUser));
    }

    public DiscordUserDto uploadAvatar(MultipartFile avatar) throws Exception {
        DiscordUser currentUser = userService.getCurrentUser();

        String location = currentUser.getAvatarLocation();
        if (location != null) {
            fileService.deleteAvatar(location);
        }
        if (avatar.isEmpty()) {
            currentUser.setAvatarLocation(null);
        } else {
            currentUser.setAvatarLocation(getLocation(avatar, currentUser));
        }

        return discordUserMapper.toDto(discordUserRepository.save(currentUser));
    }

    public byte[] getAvatar(String username) throws Exception {
        DiscordUser user = discordUserRepository.findByUsername(username)
                .orElseThrow(() -> new DiscordException("DiscordUser with username: " + username + " not found"));
        if (user.getAvatarLocation() == null)
            return fileService.getDefaultAvatar(50);
        else
            return fileService.getAvatar(user.getAvatarLocation(), 50);
    }

    private DiscordUser checkUserCredentials(CreateUserDto userDto) {
        DiscordUser user = discordUserRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new AuthenticationCredentialsNotFoundException("Password not found");
        }

        return user;
    }

    private String getLocation(MultipartFile avatar, DiscordUser discordUser) throws Exception {
        fileService.checkImageExtension(avatar);
        String filename = discordUser.getUsername() + System.currentTimeMillis();
        fileService.uploadAvatar(filename, avatar);
        return filename;
    }

    private void validateUserDto(CreateUserDto createUserDto) {
        if (discordUserRepository.existsByUsername(createUserDto.getUsername())) {
            throw new DiscordException("Username taken", StatusCode.USERNAME_TAKEN);
        }
        if (discordUserRepository.existsByEmail(createUserDto.getEmail())) {
            throw new DiscordException("Email taken", StatusCode.EMAIL_TAKEN);
        }
    }

    private void createEmailVerification(DiscordUser discordUser) {
        VerificationToken verificationToken = verificationTokenService.createToken(discordUser);
        emailService.sendMail(discordUser.getEmail(), verificationToken.getToken());
    }
}

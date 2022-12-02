package com.example.discord.service;

import com.example.discord.exception.DiscordException;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    private static final String DEFAULT_AVATAR_LOCATION = "avatar1.png";

    private static final String BUCKET = "discord";
    private static final String AVATARS = "avatars/";
    private static final String LOGOS = "logos/";

    private static final List<String> ACCEPTED_IMAGE_EXTENSIONS = List.of("png", "jpg", "jpeg");

    private final MinioClient minioClient;

    public void checkImageExtension(MultipartFile image) {
        String filename = image.getOriginalFilename();
        if (filename == null)
            return;
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        if (ACCEPTED_IMAGE_EXTENSIONS.stream().noneMatch(e -> e.equals(extension))) {
            throw new DiscordException("File extension not accepted");
        }
    }

    public void uploadAvatar(String filename, MultipartFile file) throws Exception {
        uploadFile(filename, file, AVATARS);
    }

    public byte[] getAvatar(String fileName, int size) throws Exception {
        return getFile(fileName, AVATARS, size);
    }

    public byte[] getLogo(String filename) throws Exception {
        return getFile(filename, LOGOS);
    }

    public byte[] getDefaultAvatar(int size) throws Exception {
        return getFile(DEFAULT_AVATAR_LOCATION, AVATARS, size);
    }

    public void deleteAvatar(String fileName) throws Exception {
        deleteFile(fileName, AVATARS);
    }

    public void uploadLogo(String filename, MultipartFile file) throws Exception {
        uploadFile(filename, file, LOGOS);
    }

    public byte[] getLogo(String fileName, int size) throws Exception {
        return getFile(fileName, LOGOS, size);
    }

    private byte[] getFile(String filename, String folder) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(BUCKET)
                .object(folder + filename)
                .build()).readAllBytes();
    }

    public void deleteLogo(String fileName) throws Exception {
        deleteFile(fileName, LOGOS);
    }

    private void uploadFile(String filename, MultipartFile file, String folder) throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(BUCKET)
                .object(folder + filename)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());
    }

    private void deleteFile(String filename, String folder) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(BUCKET)
                .object(folder + filename)
                .build());
    }

    private byte[] getFile(String filename, String folder, int size) throws Exception {
        BufferedImage image = ImageIO.read(minioClient.getObject(GetObjectArgs.builder()
                .bucket(BUCKET)
                .object(folder + filename)
                .build()));
        BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, size, size, null);
        graphics2D.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", baos);
        return baos.toByteArray();
    }


    public void initStructure() throws Exception {
        minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(BUCKET)
                .build());

        ClassPathResource avatar = new ClassPathResource("static/avatar1.png");
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(BUCKET)
                .object(AVATARS + DEFAULT_AVATAR_LOCATION)
                .stream(avatar.getInputStream(), avatar.getFile().getTotalSpace(), -1)
                .build());
    }

}

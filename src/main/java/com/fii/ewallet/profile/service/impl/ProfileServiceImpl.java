package com.fii.ewallet.profile.service.impl;

import com.fii.ewallet.entity.User;
import com.fii.ewallet.profile.dto.ProfileImageResponse;
import com.fii.ewallet.profile.service.ProfileService;
import com.fii.ewallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final S3Client s3Client;
    private final UserRepository userRepository;

    @Value("${aws.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Override
    public ProfileImageResponse uploadProfileImage(MultipartFile file, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getImageKey() != null) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(user.getImageKey())
                    .build());
        }

        String extension = getExtension(file.getOriginalFilename());
        String key = "profile-images/" + user.getId() + "/" + UUID.randomUUID() + extension;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to S3", e);
        }

        user.setImageKey(key);
        userRepository.save(user);

        String imageUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;

        return new ProfileImageResponse(imageUrl, "Profile image uploaded successfully", HttpStatus.OK.value(), LocalDateTime.now());
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

}

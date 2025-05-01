package com.pickgo.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Uploader implements S3Uploader {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Override
    public String upload(MultipartFile file, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }

        return "https://%s.s3.%s.amazonaws.com/%s".formatted(bucketName, region, fileName);
    }

    @Override
    public void delete(String url) {
        String key = URI.create(url).getPath().substring(1);
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try {
            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }
}

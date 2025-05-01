package com.pickgo.global.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.util.Optional;

@Configuration
public class S3Config {
    @Value("${aws.s3.accessKey}")
    private Optional<String> accessKey;

    @Value("${aws.s3.secretKey}")
    private Optional<String> secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(region));

        // 개발 환경 시 credential 세팅
        if (accessKey.isPresent() && secretKey.isPresent()) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey.get(), secretKey.get());
            builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        }

        return builder.build();
    }
}

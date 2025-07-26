package com.mwaf.productservice.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

// MinioConfig.java
@Configuration
public class MinioConfig {

    @Bean
    public S3Client minioS3(@Value("${minio.endpoint}") String endpoint,
                            @Value("${minio.accessKey}") String accessKey,
                            @Value("${minio.secretKey}") String secretKey) {

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))      // http://localhost:9000
                .region(Region.US_EAST_1)                   // any region is fine for MinIO
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)))
                // ── Path‑style so bucket isn’t put into the host name
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build())
                .build();
    }
}



package com.mwaf.productservice.service;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

// ProductImageService.java
@Service
@RequiredArgsConstructor

@Getter
@Setter
public class ProductImageService {

    private final S3Client s3;
    @Value("${minio.bucket}") private String bucket;

    public String upload(MultipartFile file, String key) throws IOException {



        /* ---------- NEW: buffer once, then pass bytes ---------- */
        byte[] bytes = file.getBytes();            // ← reads only once
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .contentLength((long) bytes.length) // good practice
                        .build(),
                RequestBody.fromBytes(bytes)        // repeatable body
        );

        return String.format("http://localhost:9000/%s/%s", bucket, key)    ;
    }
}

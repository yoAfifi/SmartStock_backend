package com.mwaf.productservice.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;          // if you use Lombok – or add getters/setters yourself

@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
}
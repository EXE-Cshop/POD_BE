package com.shirt.pod.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryConfig {
    
    private String cloudName;
    private String apiKey;
    private String apiSecret;
    
    @Bean
    public Cloudinary cloudinary() {
        log.info("=== Cloudinary Configuration ===");
        log.info("Cloud Name: {}", cloudName);
        log.info("API Key: {}", apiKey != null ? apiKey.substring(0, 4) + "***" : "null");
        
        if (cloudName == null || cloudName.isEmpty()) {
            log.error("Cloudinary cloud-name is not configured!");
            throw new IllegalStateException("Cloudinary cloud-name must be configured in application.yaml");
        }
        
        if (apiKey == null || apiKey.isEmpty()) {
            log.error("Cloudinary api-key is not configured!");
            throw new IllegalStateException("Cloudinary api-key must be configured in application.yaml");
        }
        
        if (apiSecret == null || apiSecret.isEmpty()) {
            log.error("Cloudinary api-secret is not configured!");
            throw new IllegalStateException("Cloudinary api-secret must be configured in application.yaml");
        }
        
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
        
        log.info("Cloudinary initialized successfully!");
        return cloudinary;
    }
}

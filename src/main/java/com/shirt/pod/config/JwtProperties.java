package com.shirt.pod.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "spring.security.jwt")
@Validated
@Getter
@Setter
public class JwtProperties {

    @NotBlank(message = "JWT secret must not be blank")
    private String secret;

    @Positive(message = "Access token expiration must be positive")
    private Long accessTokenExpiration;

    @Positive(message = "Refresh token expiration must be positive")
    private Long refreshTokenExpiration;

    @NotBlank(message = "Refresh token secret must not be blank")
    private String refreshTokenSecret;
}

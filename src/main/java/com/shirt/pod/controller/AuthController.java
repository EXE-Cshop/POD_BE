package com.shirt.pod.controller;

import com.shirt.pod.config.JwtProperties;
import com.shirt.pod.model.dto.request.LoginRequest;
import com.shirt.pod.model.dto.request.RefreshTokenRequest;
import com.shirt.pod.model.dto.request.RegisterRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.AuthResponse;
import com.shirt.pod.security.CustomUserDetails;
import com.shirt.pod.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refresh-token";

    private final AuthService authService;
    private final JwtProperties jwtProperties;

    @PostMapping("/register")
    @Operation(summary = "Register new account", description = "Create a new user account with email, password and full name")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);

        ResponseCookie cookie = buildRefreshTokenCookie(authResponse.getRefreshToken());
        AuthResponse bodyData = AuthResponse.builder()
                .accessToken(authResponse.getAccessToken())
                .expiresIn(authResponse.getExpiresIn())
                .user(authResponse.getUser())
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.<AuthResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("User registered successfully")
                        .data(bodyData)
                        .build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return access token; refresh token in httpOnly cookie")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        ResponseCookie cookie = buildRefreshTokenCookie(authResponse.getRefreshToken());
        AuthResponse bodyData = AuthResponse.builder()
                .accessToken(authResponse.getAccessToken())
                .expiresIn(authResponse.getExpiresIn())
                .user(authResponse.getUser())
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.<AuthResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Login successful")
                        .data(bodyData)
                        .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Use refresh token (from cookie or body) to obtain new access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshTokenFromCookie,
            @RequestBody(required = false) RefreshTokenRequest request) {

        String refreshToken = (refreshTokenFromCookie != null && !refreshTokenFromCookie.isBlank())
                ? refreshTokenFromCookie
                : (request != null && request.getRefreshToken() != null ? request.getRefreshToken() : null);

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthResponse authResponse = authService.refreshToken(
                RefreshTokenRequest.builder().refreshToken(refreshToken).build());

        ResponseCookie cookie = buildRefreshTokenCookie(authResponse.getRefreshToken());
        AuthResponse bodyData = AuthResponse.builder()
                .accessToken(authResponse.getAccessToken())
                .expiresIn(authResponse.getExpiresIn())
                .user(authResponse.getUser())
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.<AuthResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Token refreshed successfully")
                        .data(bodyData)
                        .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate refresh token and clear cookie")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            authService.logout(userDetails.getId());
        }

        ResponseCookie clearCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Logout successful")
                        .data(null)
                        .build());
    }

    private ResponseCookie buildRefreshTokenCookie(String token) {
        long maxAgeSeconds = jwtProperties.getRefreshTokenExpiration() != null
                ? jwtProperties.getRefreshTokenExpiration()
                : 604800L;
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .sameSite("Lax")
                .build();
    }
}

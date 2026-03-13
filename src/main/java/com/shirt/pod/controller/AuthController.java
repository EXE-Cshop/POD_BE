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
import org.springframework.util.StringUtils;
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

        ResponseCookie accessCookie = buildCookie("access-token", authResponse.getAccessToken(), jwtProperties.getAccessTokenExpiration());
        ResponseCookie refreshCookie = buildCookie("refresh-token", authResponse.getRefreshToken(), jwtProperties.getRefreshTokenExpiration());

        AuthResponse bodyData = AuthResponse.builder()
                .accessToken(authResponse.getAccessToken())
                .refreshToken(authResponse.getRefreshToken())
                .expiresIn(authResponse.getExpiresIn())
                .user(authResponse.getUser())
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
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

        ResponseCookie accessCookie = buildCookie("access-token", authResponse.getAccessToken(), jwtProperties.getAccessTokenExpiration());
        ResponseCookie refreshCookie = buildCookie("refresh-token", authResponse.getRefreshToken(), jwtProperties.getRefreshTokenExpiration());

        AuthResponse bodyData = AuthResponse.builder()
                .accessToken(authResponse.getAccessToken())
                .refreshToken(authResponse.getRefreshToken())
                .expiresIn(authResponse.getExpiresIn())
                .user(authResponse.getUser())
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
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

                String refreshToken = null;
                if (request != null && StringUtils.hasText(request.getRefreshToken())) {
                        refreshToken = request.getRefreshToken();
                } else if (StringUtils.hasText(refreshTokenFromCookie)) {
                        refreshToken = refreshTokenFromCookie;
                }

                if (refreshToken == null || refreshToken.isBlank()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }

                AuthResponse authResponse = authService.refreshToken(
                                RefreshTokenRequest.builder().refreshToken(refreshToken).build());

        ResponseCookie accessCookie = buildCookie("access-token", authResponse.getAccessToken(), jwtProperties.getAccessTokenExpiration());
        ResponseCookie refreshCookie = buildCookie("refresh-token", authResponse.getRefreshToken(), jwtProperties.getRefreshTokenExpiration());

        AuthResponse bodyData = AuthResponse.builder()
                .accessToken(authResponse.getAccessToken())
                .refreshToken(authResponse.getRefreshToken())
                .expiresIn(authResponse.getExpiresIn())
                .user(authResponse.getUser())
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
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

        ResponseCookie clearAccessCookie = buildCookie("access-token", "", 0L);
        ResponseCookie clearRefreshCookie = buildCookie("refresh-token", "", 0L);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString())
                .body(ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Logout successful")
                        .data(null)
                        .build());
        }

    private ResponseCookie buildCookie(String name, String value, Long maxAgeSeconds) {
        return ResponseCookie.from(name, value != null ? value : "")
                .httpOnly(true)
                .secure(false) // Set to true only for HTTPS production
                .path("/")
                .maxAge(maxAgeSeconds != null ? maxAgeSeconds : 0)
                .sameSite("Lax") // More compatible for same-site (localhost) development
                .build();
    }
}

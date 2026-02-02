package com.shirt.pod.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new account", description = "Create a new user account with email, password and full name")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);

        return ApiResponse.<AuthResponse>builder()
                .code(HttpStatus.OK.value())
                .message("User registered successfully")
                .data(authResponse)
                .build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return access token with refresh token")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        return ApiResponse.<AuthResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Login successful")
                .data(authResponse)
                .build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Use refresh token to obtain new access token when the old one expires")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request);

        return ApiResponse.<AuthResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Token refreshed successfully")
                .data(authResponse)
                .build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate the refresh token of the current user")
    public ApiResponse<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getId());

        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Logout successful")
                .build();
    }
}

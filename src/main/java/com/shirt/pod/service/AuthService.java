package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.LoginRequest;
import com.shirt.pod.model.dto.request.RefreshTokenRequest;
import com.shirt.pod.model.dto.request.RegisterRequest;
import com.shirt.pod.model.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(Long userId);
}

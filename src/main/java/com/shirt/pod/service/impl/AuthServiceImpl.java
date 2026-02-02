package com.shirt.pod.service.impl;

import com.shirt.pod.config.JwtProperties;
import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.mapper.UserMapper;
import com.shirt.pod.model.dto.request.LoginRequest;
import com.shirt.pod.model.dto.request.RefreshTokenRequest;
import com.shirt.pod.model.dto.request.RegisterRequest;
import com.shirt.pod.model.dto.response.AuthResponse;
import com.shirt.pod.model.dto.response.UserDTO;
import com.shirt.pod.model.entity.RefreshToken;
import com.shirt.pod.model.entity.Role;
import com.shirt.pod.model.entity.User;
import com.shirt.pod.model.entity.enums.RoleConstant;
import com.shirt.pod.model.entity.enums.UserStatus;
import com.shirt.pod.repository.RoleRepository;
import com.shirt.pod.repository.UserRepository;
import com.shirt.pod.security.CustomUserDetails;
import com.shirt.pod.security.JwtTokenProvider;
import com.shirt.pod.service.AuthService;
import com.shirt.pod.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider tokenProvider;
        private final RefreshTokenService refreshTokenService;
        private final UserMapper userMapper;
        private final JwtProperties jwtProperties;

        @Override
        @Transactional
        public AuthResponse login(LoginRequest request) {
                // not throw exception -> ok
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                String accessToken = tokenProvider.generateAccessToken(authentication);

                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                User user = userRepository.findById(userDetails.getId())
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "id",
                                                userDetails.getId()));

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

                UserDTO userDTO = userMapper.toDTO(user);

                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getToken())
                                .expiresIn(jwtProperties.getAccessTokenExpiration())
                                .user(userDTO)
                                .build();
        }

        @Override
        @Transactional
        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS, request.getEmail());
                }

                Role userRole = roleRepository.findByName(RoleConstant.USER.name())
                                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "name",
                                                RoleConstant.USER.name()));

                Set<Role> roles = new HashSet<>();
                roles.add(userRole);

                User user = User.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .fullName(request.getFullName())
                                .phoneNumber(request.getPhoneNumber())
                                .status(UserStatus.ACTIVE)
                                // .provider("LOCAL")
                                .roles(roles)
                                .build();

                User savedUser = userRepository.save(user);

                String accessToken = tokenProvider.generateAccessTokenFromEmail(
                                savedUser.getEmail(),
                                savedUser.getId(),
                                savedUser.getRoles());

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

                UserDTO userDTO = userMapper.toDTO(savedUser);

                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getToken())
                                .expiresIn(jwtProperties.getAccessTokenExpiration())
                                .user(userDTO)
                                .build();
        }

        @Override
        @Transactional
        public AuthResponse refreshToken(RefreshTokenRequest request) {
                RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
                refreshToken = refreshTokenService.verifyExpiration(refreshToken);

                User user = refreshToken.getUser();

                refreshTokenService.deleteRefreshToken(refreshToken);
                RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

                String accessToken = tokenProvider.generateAccessTokenFromEmail(
                                user.getEmail(),
                                user.getId(),
                                user.getRoles());

                UserDTO userDTO = userMapper.toDTO(user);

                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(newRefreshToken.getToken())
                                .expiresIn(jwtProperties.getAccessTokenExpiration())
                                .user(userDTO)
                                .build();
        }

        @Override
        @Transactional
        public void logout(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "id", userId));

                refreshTokenService.deleteByUser(user);
        }
}
